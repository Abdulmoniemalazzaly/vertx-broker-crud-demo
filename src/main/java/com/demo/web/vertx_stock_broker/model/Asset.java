package com.demo.web.vertx_stock_broker.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
  String symbol;
}
