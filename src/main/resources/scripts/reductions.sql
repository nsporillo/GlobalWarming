CREATE TABLE IF NOT EXISTS reductions (
  uniqueId INT PRIMARY KEY,
  reductionerId INT NOT NULL,
  reductionKey INT NOT NULL,
  worldId VARCHAR(36) NOT NULL,
  value SMALLINT NOT NULL
);