package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.WatchList;
import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {

  private final HashMap<UUID , WatchList> watchListPerAccount;

  public PutWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);

    JsonObject json = context.body().asJsonObject();
    WatchList watchList = json.mapTo(WatchList.class);
    watchListPerAccount.put(UUID.fromString(accountId) , watchList);
    context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(json.toBuffer());
  }
}
