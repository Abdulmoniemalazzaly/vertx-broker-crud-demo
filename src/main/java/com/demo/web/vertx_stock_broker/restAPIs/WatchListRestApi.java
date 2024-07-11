package com.demo.web.vertx_stock_broker.restAPIs;

import com.demo.web.vertx_stock_broker.handlers.*;
import com.demo.web.vertx_stock_broker.model.WatchList;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {

  private final static Logger logger = LoggerFactory.getLogger(WatchListRestApi.class);
  public static void attach(Router parent , Pool pool) {
    final HashMap<UUID , WatchList> watchListPerAccount = new HashMap<UUID, WatchList>();
    final String path = "/account/watchlist/:accountId";

    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));

    final String dbPath = "/db/account/watchlist/:accountId";
    parent.get(dbPath).handler(new GetWatchListFromDbHandler(pool));
    parent.put(dbPath).handler(new PutWatchListToDbHandler(pool));
    parent.delete(dbPath).handler(new DeleteWatchListFromDbHandler(pool));
  }

  public static String getAccountId(RoutingContext context) {
    final String accountId = context.pathParam("accountId");
    logger.debug("{} for account {} " , context.normalizedPath() , accountId);
    return accountId;
  }
}
