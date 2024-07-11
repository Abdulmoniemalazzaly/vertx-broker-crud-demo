package com.demo.web.vertx_stock_broker.config.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private final static Logger logger = LoggerFactory.getLogger(FlywayMigration.class);
  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {
    return vertx.executeBlocking(() -> execute(dbConfig))
      .onFailure(error -> logger.error("Migration failed {} " , error))
      .onSuccess(res -> logger.info("Migration done!"));
  }

  private static Void execute(DbConfig dbConfig) {
    final String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s" ,
      dbConfig.getHost() , dbConfig.getPort() , dbConfig.getDatabase());

    logger.debug("Migration DB schema using url: {} " , jdbcUrl);

    final Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl , dbConfig.getUser() , dbConfig.getPassword())
//      .schemas("broker")
//      .defaultSchema("broker")
      .load();

    var current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(info -> logger.info("db schema is at version: {}" , info.getVersion()));
    var pendingMigrations = flyway.info().pending();
    logger.debug("Pending migrations are: {}" , printMigrations(pendingMigrations));
    flyway.migrate();
    return null;
  }

  private static String printMigrations(MigrationInfo[] pending) {
    if (Objects.isNull(pending)){
      return "[]";
    }
    return Arrays.stream(pending)
      .map(each -> each.getVersion() + " - " + each.getDescription())
      .collect(Collectors.joining("," , "[" ,"]"));
  }
}
