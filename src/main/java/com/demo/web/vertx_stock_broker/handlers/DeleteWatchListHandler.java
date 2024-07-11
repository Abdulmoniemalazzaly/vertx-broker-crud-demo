package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.WatchList;
import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private final HashMap<UUID , WatchList> watchListPerAccount;

  public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);
    final WatchList removed = watchListPerAccount.remove(UUID.fromString(accountId));
    context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(removed.toJsonObject().toBuffer());
  }
}
