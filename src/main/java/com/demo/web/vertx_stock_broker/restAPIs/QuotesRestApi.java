package com.demo.web.vertx_stock_broker.restAPIs;

import com.demo.web.vertx_stock_broker.handlers.GetQuoteFromDbHandler;
import com.demo.web.vertx_stock_broker.handlers.GetQuoteHandler;
import com.demo.web.vertx_stock_broker.model.Asset;
import com.demo.web.vertx_stock_broker.model.Quote;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router parent, final Pool pool) {
    final Map<String , Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(asset -> cachedQuotes.put(asset , initRandomQuote(asset)));
    parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
    parent.get("/db/quotes/:asset").handler(new GetQuoteFromDbHandler(pool));
  }

  private static Quote initRandomQuote(String asset) {
    return Quote.builder()
      .asset(Asset.builder().symbol(asset).build())
      .bid(randomValue())
      .ask(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1 , 100));
  }
}
