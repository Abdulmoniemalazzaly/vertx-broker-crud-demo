package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.WatchList;
import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {
  private final HashMap<UUID , WatchList> watchListPerAccount;

  public GetWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount){
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);
    var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
    if (watchList.isEmpty()){
      context
        .response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message" , "watchlist for account " + accountId + " not found!")
          .put("path" , context.normalizedPath())
          .toBuffer()
        );
      return;
    }
    context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(watchList.get().toJsonObject().toBuffer());
  }
}
