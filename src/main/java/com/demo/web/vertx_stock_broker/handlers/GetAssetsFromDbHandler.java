package com.demo.web.vertx_stock_broker.handlers;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDbHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(GetAssetsFromDbHandler.class);
  private final Pool db;

  public GetAssetsFromDbHandler(final Pool pool) {
    this.db = pool;
  }

  @Override
  public void handle(RoutingContext context) {
    db.query("SELECT a.value FROM assets a")
      .execute()
      .onFailure(DbResponseHandler.errorHandler(context , "Failed tp get assets from db"))
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          response.add(row.getValue("value"));
        });

        logger.info("Path {} responses {} " , context.normalizedPath() , response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());

      });
  }
}
