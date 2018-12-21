CREATE TABLE IF NOT EXISTS contributions (
  uniqueId INT PRIMARY KEY,
  contributerId INT NOT NULL,
  contributionKey INT NOT NULL,
  worldId VARCHAR(36) NOT NULL,
  time TIMESTAMP NOT NULL DEFAULT now(),
  value SMALLINT NOT NULL
);