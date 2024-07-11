package com.demo.web.vertx_stock_broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigLoader {

  public static final String SERVER_PORT = "SERVER_PORT";
  public static final List<String> EXPOSED_ENV_VARIABLES = List.of(SERVER_PORT);
  public static final String ENV = "env";
  public static final String KEYS = "keys";
  private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
  public static final String SYS = "sys";
  public static final String CACHE = "cache";
  public static final String FILE = "file";
  public static final String YAML = "yaml";
  public static final String PATH = "path";
  public static final String CONFIG_FILE = "application.yml";

  public static final String DB_HOST = "db_host";
  public static final String DB_PORT = "db_port";
  public static final String DB_DATABASE = "db_database";
  public static final String DB_USER = "db_user";
  public static final String DB_PASSWORD = "db_password";

  public static Future<BrokerConfig> load(Vertx vertx){
    final var exposedKeys = new JsonArray();
    EXPOSED_ENV_VARIABLES.forEach(exposedKeys::add);
    logger.debug("Fetch configuration from {} " , exposedKeys.encode());

    var envStore = new ConfigStoreOptions()
      .setType(ENV)
      .setConfig(new JsonObject().put(KEYS, exposedKeys));

    var propertyStore= new ConfigStoreOptions()
      .setType(SYS)
      .setConfig(new JsonObject().put(CACHE, false));

    var yamlStore = new ConfigStoreOptions()
      .setType(FILE)
      .setFormat(YAML)
      .setConfig(new JsonObject().put(PATH, CONFIG_FILE));

    var retriever = ConfigRetriever.create(vertx ,
      new ConfigRetrieverOptions()
        .addStore(yamlStore)
        .addStore(envStore)
        .addStore(propertyStore)
    );
    return retriever.getConfig().map(BrokerConfig::from);
  }
}
