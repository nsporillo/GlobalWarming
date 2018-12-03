CREATE TABLE IF NOT EXISTS offsets (
  uniqueId INT PRIMARY KEY,
  creatorId INT NOT NULL,
  hunterId INT,
  worldId VARCHAR(36) NOT NULL,
  logBlocksTarget SMALLINT NOT NULL,
  reward INT NOT NULL,
  timeStarted BIGINT NOT NULL,
  timeCompleted BIGINT
);