package db.migration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import java.util.Map;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V0002__initial_import extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {

    try (DSLContext dsl = using(context.getConnection())) {
      Map<String, Long> roleNameToId = dsl.selectFrom(table("APP_ROLE"))
          .fetchMap(field("NAME", String.class), field("ID", Long.class));

      PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

      dsl.transaction(txConf -> {
        try (var txdsl = DSL.using(txConf)) {
          txdsl
              .insertInto(table("APP_USER"), field("ID"), field("FIRST_NAME"),
                  field("LAST_NAME"), field("USER_NAME"), field("EMAIL"),
                  field("PASSWORD_HASH"), field("ENABLED"))
              .values(1, "admin", "admin", "admin", "admin@test.com", pe.encode("admin"),
                  true)
              .execute();

          txdsl.insertInto(table("APP_USER_ROLES"), field("APP_USER_ID"),
              field("APP_ROLE_ID")).values(1, roleNameToId.get("ADMIN")).execute();
        }
      });
    }

  }
}