CREATE TABLE `watchlist`
(
  `account_id` VARCHAR(50),
  `asset` VARCHAR(50),
  FOREIGN KEY (asset) REFERENCES assets (`VALUE`),
  PRIMARY KEY (`account_id` , asset)
);

