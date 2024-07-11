package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.Quote;
import com.demo.web.vertx_stock_broker.model.QuoteEntity;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class GetQuoteFromDbHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(GetQuoteFromDbHandler.class);
  private final Pool db;

  public GetQuoteFromDbHandler(Pool pool) {
    this.db = pool;
  }


  @Override
  public void handle(RoutingContext context) {
    final String asset = context.pathParam("asset");
    logger.debug("Asset parameter {} " , asset);
    SqlTemplate.forQuery(db ,
        "SELECT q.asset , q.bid , q.ask , q.last_price , q.volume FROM quotes q WHERE q.asset=#{asset} ")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset" , asset))
      .onFailure(DbResponseHandler.errorHandler(context , "Failed tp get quote for asset "+ asset +" from db!"))
      .onSuccess(quotes -> {
          if (!quotes.iterator().hasNext()){
              DbResponseHandler.notFound(context , "quote for asset "+ asset +" not found!");
              return;
          }
        final var response = quotes.iterator().next().toJsonObject();
        logger.info("Path {} responses {} " , context.normalizedPath() , response.encode());
        context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(response.toBuffer());
      });
  }
}
