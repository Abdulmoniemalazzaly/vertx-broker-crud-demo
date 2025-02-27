package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.Quote;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {

  private final static Logger logger = LoggerFactory.getLogger(GetQuoteHandler.class);

  private final Map<String , Quote> cachedQuotes;

  public GetQuoteHandler(Map<String, Quote> cachedQuotes){
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
    final String asset = context.pathParam("asset");
    logger.debug("Asset parameter {} " , asset);
    Optional<Quote> maybeQuote = Optional.ofNullable(cachedQuotes.get(asset));
    if (maybeQuote.isEmpty()){
      context
        .response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message" , "quote for asset " + asset + " not found!")
          .put("path" , context.normalizedPath())
          .toBuffer()
        );
      return;
    }
    final JsonObject response = maybeQuote.get().toJsonObject();
    logger.info("Path {} responses {} " , context.normalizedPath() , response.encode());
    context.response().putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON).end(response.toBuffer());
  }
}
