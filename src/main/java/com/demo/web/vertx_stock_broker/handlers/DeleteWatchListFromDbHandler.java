package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeleteWatchListFromDbHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(DeleteWatchListFromDbHandler.class);

  private final Pool db;

  public DeleteWatchListFromDbHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);

    SqlTemplate.forUpdate(db,
      "DELETE FROM watchlist w WHERE w.account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id" , accountId))
      .onFailure(DbResponseHandler.errorHandler(context , "Failed to delete watchlist for account id " + accountId))
      .onSuccess(result -> {
        logger.debug("deleted watchlist for account {} is {}" , accountId , result.rowCount());
        context.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
