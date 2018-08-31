CREATE TABLE IF NOT EXISTS contributions (
  uniqueID BIGINT PRIMARY KEY,
  contributerId BIGINT NOT NULL,
  contributionKey BIGINT NOT NULL,
  worldName VARCHAR(255) NOT NULL,
  value DOUBLE NOT NULL
);