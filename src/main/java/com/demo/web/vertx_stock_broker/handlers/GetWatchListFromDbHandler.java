package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchListFromDbHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(GetWatchListFromDbHandler.class);

  private final Pool db;

  public GetWatchListFromDbHandler(final Pool pool) {
    this.db = pool;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);
    SqlTemplate.forQuery(db,
      "SELECT w.asset FROM watchlist w WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id" , accountId))
      .onFailure(DbResponseHandler.errorHandler(context , "Failed tp get watchlist for account id "+ accountId +" from db!"))
      .onSuccess(assets ->{
        if (!assets.iterator().hasNext()){
          DbResponseHandler.notFound(context , "watchlist for account id " + accountId + " not found!");
          return;
        }
        final var response = new JsonArray();
        assets.forEach(response::add);
        logger.info("Path {} responses {} " , context.normalizedPath() , response.encode());
        context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(response.toBuffer());
      });
  }
}
