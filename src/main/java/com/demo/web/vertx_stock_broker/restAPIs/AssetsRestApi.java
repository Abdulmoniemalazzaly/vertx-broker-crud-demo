package com.demo.web.vertx_stock_broker.restAPIs;

import com.demo.web.vertx_stock_broker.handlers.GetAssetsFromDbHandler;
import com.demo.web.vertx_stock_broker.handlers.GetAssetsHandler;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {
  public final static List<String> ASSETS = Arrays.asList("AAPL","AMZN","NFLX","TSLA");
  public static void attach(Router parent, final Pool pool){
    parent.get("/assets").handler(new GetAssetsHandler());
    parent.get("/db/assets").handler(new GetAssetsFromDbHandler(pool));
  }
}
