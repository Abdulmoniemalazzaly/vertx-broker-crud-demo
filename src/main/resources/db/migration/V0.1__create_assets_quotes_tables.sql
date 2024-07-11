CREATE TABLE `assets`
(
  `VALUE` VARCHAR(50),
  PRIMARY KEY (`VALUE`)
);

CREATE TABLE `quotes`
(
  `bid` DECIMAL,
  `ask` DECIMAL,
  `last_price` DECIMAL,
  `volume` DECIMAL,
  `asset` varchar(50),
  FOREIGN KEY (asset) REFERENCES assets (`VALUE`),
  CONSTRAINT last_price_is_positive CHECK ( `last_price` > 0 ),
  CONSTRAINT volume_is_positive_or_zero CHECK ( `volume` >= 0 )
);

