CREATE TABLE IF NOT EXISTS contributions (
  uniqueID BIGINT PRIMARY KEY,
  contributerId BIGINT,
  contributionKey BIGINT,
  worldName VARCHAR(255),
  value DOUBLE
);