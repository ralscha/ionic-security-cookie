package ch.rasc.jwt.db;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import ch.rasc.jwt.AppConfig;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

@Component
public class XodusManager {

  public static final String USER = "User";

  private final PersistentEntityStore persistentEntityStore;

  public XodusManager(AppConfig appConfig) {
    this.persistentEntityStore = PersistentEntityStores
        .newInstance(appConfig.getXodusPath().toFile());
  }

  @PreDestroy
  public void destroy() {
    if (this.persistentEntityStore != null) {
      this.persistentEntityStore.close();
    }
  }

  public boolean hasUsers() {
    return this.persistentEntityStore.computeInTransaction(txn -> {
      return !txn.getAll(USER).isEmpty();
    });
  }

  public void printAllUsers() {
    this.persistentEntityStore.executeInTransaction(txn -> {
      txn.getAll(USER).forEach(System.out::println);
    });
  }

  public void deleteInactiveUsers(long timestamp) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      txn.find(USER, "lastAccess", 0, timestamp).forEach(entity -> {
        if (!entity.getProperty("username").equals("admin")) {
          entity.delete();
        }
      });
    });
  }

  public User fetchUser(String username) {
    return this.persistentEntityStore.computeInReadonlyTransaction(txn -> {
      Entity entity = txn.find(USER, "username", username).getFirst();
      if (entity != null) {
        return fromEntity(entity);
      }
      return null;
    });
  }

  public void resetLockedProperties(String username) {
    this.persistentEntityStore.executeInTransaction(txn -> {
      Entity entity = txn.find(USER, "username", username).getFirst();
      if (entity != null) {
        entity.deleteProperty("failedLogins");
        entity.deleteProperty("lockedOutUntil");
      }
    });
  }

  public boolean updateLockedProperties(String username, Integer loginLockAttempts,
      Integer loginLockMinutes) {

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

        if (failedLogins >= loginLockAttempts) {
          if (loginLockMinutes != null) {
            entity.setProperty("lockedOutUntil", ZonedDateTime.now(ZoneOffset.UTC)
                .plusMinutes(loginLockMinutes).toInstant().getEpochSecond());
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
      toEntity(user, entity);
    });
  }

  private User fromEntity(Entity entity) {
    User user = new User();    
    for (String fieldName : this.userFieldAccess.getFieldNames()) {
      this.userFieldAccess.set(user, fieldName, entity.getProperty(fieldName));
    }
    return user;
  }

  private void toEntity(User user, Entity entity) {
    for (String fieldName : this.userFieldAccess.getFieldNames()) {
      @SuppressWarnings("rawtypes")
      Comparable value = (Comparable) this.userFieldAccess.get(user, fieldName);
      if (value != null) {
        entity.setProperty(fieldName, value);
      }
      else {
        entity.deleteProperty(fieldName);
      }
    }
  }

}
