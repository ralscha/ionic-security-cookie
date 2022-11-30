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

    @SuppressWarnings("resource")
    DSLContext dsl = using(context.getConnection());
    Map<String, Long> roleNameToId = dsl.selectFrom(table("app_role"))
        .fetchMap(field("name", String.class), field("id", Long.class));

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    dsl.transaction(txConf -> {
      var txdsl = DSL.using(txConf);
      txdsl
          .insertInto(table("app_user"), field("id"), field("first_name"),
              field("last_name"), field("user_name"), field("email"),
              field("password_hash"), field("enabled"))
          .values(1, "admin", "admin", "admin", "admin@test.com", pe.encode("admin"),
              true)
          .execute();

      txdsl
          .insertInto(table("app_user_roles"), field("app_user_id"), field("app_role_id"))
          .values(1, roleNameToId.get("ADMIN")).execute();
    });

  }

}