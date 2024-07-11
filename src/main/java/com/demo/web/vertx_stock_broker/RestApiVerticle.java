package com.demo.web.vertx_stock_broker;

import com.demo.web.vertx_stock_broker.config.BrokerConfig;
import com.demo.web.vertx_stock_broker.config.ConfigLoader;
import com.demo.web.vertx_stock_broker.restAPIs.AssetsRestApi;
import com.demo.web.vertx_stock_broker.restAPIs.QuotesRestApi;
import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        logger.info("Retrieved Configuration: {} " , config);
        startHttpServerAndAttachRoutes(startPromise , config);
      });
  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, BrokerConfig config) {
    final Router restApi = Router.router(vertx);

    final Pool pool = createDbPool(config);

    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());
    AssetsRestApi.attach(restApi , pool);
    QuotesRestApi.attach(restApi , pool);
    WatchListRestApi.attach(restApi, pool);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> logger.error("HTTP server error " + error))
      .listen(config.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          logger.info("HTTP server started on port {} " , config.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Pool createDbPool(BrokerConfig config) {
    final MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(config.getDbConfig().getPort())
      .setHost(config.getDbConfig().getHost())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());

    final PoolOptions poolOptions = new PoolOptions().setMaxSize(4);

    return MySQLBuilder.pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }

  private Handler<RoutingContext> handleFailure() {
    return errorCntx -> {
      if (errorCntx.response().ended()) {
        return;
      }
      logger.error("Route error ", errorCntx.failure());
      errorCntx.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
