package ch.rasc.security.db;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import ch.rasc.security.config.AppProperties;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

@Component
public class XodusManager {

  private static final String USER = "User";

  private static final String REMEMBER_ME_TOKEN = "RememberMeToken";

  private final PersistentEntityStore persistentEntityStore;

  private final AppProperties appProperties;

  public XodusManager(AppProperties appProperties) {
    this.persistentEntityStore = PersistentEntityStores
        .newInstance(appProperties.getXodusPath().toFile());
    this.appProperties = appProperties;
  }

  @PreDestroy
  public void destroy() {
    if (this.persistentEntityStore != null) {
      this.persistentEntityStore.close();
    }
  }

  public boolean changePassword(String token, String hashedPassword) {
    return this.persistentEntityStore.computeInTransaction(txn -> {
      Entity entity = txn.find(USER, "passwordResetToken", token).getFirst();
      if (entity != null) {
        Long validUntil = (Long) entity.getProperty("passwordResetTokenValidUntil");
        entity.deleteProperty("passwordResetToken");
        entity.deleteProperty("passwordResetTokenValidUntil");
        if (validUntil != null && validUntil > Instant.now().getEpochSecond()) {
          entity.setProperty("password", hashedPassword);
          return true;
        }
      }
      return false;
    });
  }

  public RememberMeToken fetchToken(String series) {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      Entity entity = txn.find(REMEMBER_ME_TOKEN, "series", series).getFirst();
      if (entity != null) {
        return RememberMeToken.fromEntity(entity);
      }
      return null;
    });
  }

  public List<RememberMeToken> fetchTokens(String username) {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      return StreamSupport
          .stream(txn.find(REMEMBER_ME_TOKEN, "username", username).spliterator(), false)
          .map(RememberMeToken::fromEntity).collect(Collectors.toList());
    });
  }

  public void persistToken(RememberMeToken token) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(REMEMBER_ME_TOKEN, "series", token.getSeries()).getFirst();
      if (entity == null) {
        entity = txn.newEntity(REMEMBER_ME_TOKEN);
      }
      token.toEntity(entity);
    });
  }

  public void deleteToken(RememberMeToken token) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(REMEMBER_ME_TOKEN, "series", token.getSeries()).getFirst();
      if (entity != null) {
        entity.delete();
      }
    });
  }

  public void deleteToken(String username, String series) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(REMEMBER_ME_TOKEN, "series", series).getFirst();
      if (entity != null) {
        if (entity.getProperty("username").equals(username)) {
          entity.delete();
        }
      }
    });
  }

  public boolean hasUsers() {
    return this.persistentEntityStore.computeInTransaction(txn -> {
      return !txn.getAll(USER).isEmpty();
    });
  }

  public void printAllUsers() {
    this.persistentEntityStore.executeInTransaction(txn -> {
      txn.getAll(USER).forEach(e -> System.out.println(User.fromEntity(e)));
    });
  }

  public List<User> fetchAllUsers() {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      return StreamSupport.stream(txn.getAll(USER).spliterator(), false)
          .map(User::fromEntity).collect(Collectors.toList());
    });
  }

  public void deleteInactiveUsers(long timestamp) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      txn.find(USER, "lastAccess", 0, timestamp).forEach(entity -> {
        String username = (String) entity.getProperty("username");
        if (!username.equals("admin")) {
          txn.find(REMEMBER_ME_TOKEN, "username", username).forEach(rememberMeEntity -> {
            rememberMeEntity.delete();
          });
          entity.delete();
        }
      });
    });
  }

  public User generatePasswordResetToken(String usernameOrEmail) {
    return this.persistentEntityStore.computeInTransaction(txn -> {
      Entity entity = txn.find(USER, "username", usernameOrEmail).getFirst();
      if (entity == null) {
        entity = txn.find(USER, "email", usernameOrEmail).getFirst();
      }
      if (entity != null) {
        String token = UUID.randomUUID().toString();
        token = Base64.getUrlEncoder()
            .encodeToString(token.getBytes(StandardCharsets.UTF_8));
        entity.setProperty("passwordResetToken", token);
        entity.setProperty("passwordResetTokenValidUntil",
            ZonedDateTime.now(ZoneOffset.UTC).plusHours(4).toInstant().getEpochSecond());
        return User.fromEntity(entity);
      }
      return null;
    });
  }

  public User fetchUser(String username) {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      Entity entity = txn.find(USER, "username", username).getFirst();
      if (entity != null) {
        return User.fromEntity(entity);
      }
      return null;
    });
  }

  public void resetLockedProperties(String username, boolean setLastAccess) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(USER, "username", username).getFirst();
      if (entity != null) {
        if (setLastAccess) {
          entity.setProperty("lastAccess", Instant.now().getEpochSecond());
        }
        entity.deleteProperty("failedLogins");
        entity.deleteProperty("lockedOutUntil");
      }
    });
  }

  public boolean updateLockedProperties(String username) {

    return this.persistentEntityStore.computeInTransaction(txn -> {
      Entity entity = txn.find(USER, "username", username).getFirst();
      if (entity != null) {

        Integer failedLogins = (Integer) entity.getProperty("failedLogins");
        if (failedLogins == null) {
          failedLogins = 1;
          entity.setProperty("failedLogins", failedLogins);
        }
        else {
          failedLogins++;
          entity.setProperty("failedLogins", failedLogins);
        }

        if (failedLogins >= this.appProperties.getLoginLockAttempts()) {
          if (this.appProperties.getLoginLockMinutes() != null) {
            entity.setProperty("lockedOutUntil",
                ZonedDateTime.now(ZoneOffset.UTC)
                    .plusMinutes(this.appProperties.getLoginLockMinutes()).toInstant()
                    .getEpochSecond());
          }
          else {
            entity.setProperty("lockedOutUntil", ZonedDateTime.now(ZoneOffset.UTC)
                .plusYears(1000).toInstant().getEpochSecond());
          }
        }
        return true;
      }
      return false;
    });
  }

  public boolean userExists(String username) {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      return !txn.find(USER, "username", username).isEmpty();
    });
  }

  public void persistUser(User user) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(USER, "username", user.getUsername()).getFirst();
      if (entity == null) {
        entity = txn.newEntity(USER);
      }
      user.toEntity(entity);
    });
  }

}
