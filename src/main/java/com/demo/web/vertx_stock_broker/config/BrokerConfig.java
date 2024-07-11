package com.demo.web.vertx_stock_broker.config;

import com.demo.web.vertx_stock_broker.config.db.DbConfig;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  public static final String VERSION = "version";
  int serverPort;
  String version;
  DbConfig dbConfig;

  public static BrokerConfig from(final JsonObject config){
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
    final String version = config.getString(VERSION);
    if (Objects.isNull(serverPort)){
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
    } else if (Objects.isNull(version)) {
      throw new RuntimeException("version is not configured in config file!");
    }
    return BrokerConfig.builder()
      .serverPort(serverPort)
      .dbConfig(parseDbConfig(config))
      .version(version)
      .build();
  }

  private static DbConfig parseDbConfig(JsonObject config) {
    return DbConfig.builder()
      .host(config.getString(ConfigLoader.DB_HOST))
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .user(config.getString(ConfigLoader.DB_USER))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .build();
  }
}
