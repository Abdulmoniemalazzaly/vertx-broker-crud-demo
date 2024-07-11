package com.demo.web.vertx_stock_broker.handlers;

import com.demo.web.vertx_stock_broker.model.Asset;
import com.demo.web.vertx_stock_broker.restAPIs.AssetsRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.demo.web.vertx_stock_broker.restAPIs.AssetsRestApi.ASSETS;

public class GetAssetsHandler implements Handler<RoutingContext> {
  private final static Logger logger = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext context) {
    final JsonArray response = new JsonArray();
    ASSETS.stream().map( s -> Asset.builder().symbol(s).build()).forEach(response::add);
    logger.info("Path {} responses {} " , context.normalizedPath() , response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE , HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer());
  }
}
