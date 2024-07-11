package com.demo.web.vertx_stock_broker.config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfoVericle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(VersionInfoVericle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        logger.info("Current Application Version is: {} " , config.getVersion());
      });
    startPromise.complete();
  }
}
