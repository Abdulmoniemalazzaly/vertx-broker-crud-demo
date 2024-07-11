package com.demo.web.vertx_stock_broker;

import com.demo.web.vertx_stock_broker.config.ConfigLoader;
import com.demo.web.vertx_stock_broker.config.VersionInfoVericle;
import com.demo.web.vertx_stock_broker.config.db.FlywayMigration;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  public static final int PORT = 8888;
  private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    System.setProperty(ConfigLoader.SERVER_PORT , "9000");
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      logger.error("unhandled " + error);
    });
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err -> logger.error("failed to deploy " + err))
      .onSuccess(id -> logger.info("Deployed {} with id {} " , RestApiVerticle.class.getName() , id));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(VersionInfoVericle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(id -> logger.info("Deployed {} with id {} " , VersionInfoVericle.class.getName() , id))
      .compose(next -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id -> logger.info("Migrated db to the latest version!"))
      .compose(next -> deployRestApiVerticle(startPromise));
  }

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> FlywayMigration.migrate(vertx , config.getDbConfig()));
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName() ,
        new DeploymentOptions().setInstances(4))
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        logger.info("Deployed {} with id {} " , RestApiVerticle.class.getName() , id);
        startPromise.complete();
      });
  }

  private int getInstances() {
    return Math.max(1 , Runtime.getRuntime().availableProcessors());
  }


}
