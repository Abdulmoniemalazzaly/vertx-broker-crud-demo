package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.WatchList;
import com.demo.web.vertx_stock_broker.restAPIs.WatchListRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PutWatchListToDbHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(PutWatchListToDbHandler.class);

  private final Pool db;

  public PutWatchListToDbHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String accountId = WatchListRestApi.getAccountId(context);

    JsonObject json = context.body().asJsonObject();
    WatchList watchList = json.mapTo(WatchList.class);

    var parameterBatch = watchList.getAssets().stream()
      .map(asset -> {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", accountId);
        parameters.put("asset" , asset);
        return parameters;
      }).collect(Collectors.toList());

    db.withConnection(client -> {
      return SqlTemplate.forUpdate(client ,
        "DELETE FROM watchlist w WHERE w.account_id=#{account_is}")
        .execute(Collections.singletonMap("account_id" , accountId))
        .onFailure(DbResponseHandler.errorHandler(context , "Failed to delete all watchlist for account id " + accountId))
          .compose(deletetionDone -> updateWatchList(client ,context, parameterBatch))
        .onFailure(DbResponseHandler.errorHandler(context , "Failed to update watchlist for account id " + accountId))
        .onSuccess(result -> {
          if (!context.response().ended()) {
            context.response()
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end();
          }
        });
    });
  }

  private Future<SqlResult<Void>> updateWatchList(SqlConnection client, RoutingContext context, List<Map<String, Object>> parameterBatch) {
    return SqlTemplate.forUpdate(client,
        "INSERT INTO watchlist VALUES (#{account_id},#{asset}) ON CONFLICT (account_id , asset) DO NOTHING")
      .executeBatch(parameterBatch)
      .onFailure(DbResponseHandler.errorHandler(context, "Failed to add to watchlist"));
  }
}
