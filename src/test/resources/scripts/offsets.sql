CREATE TABLE IF NOT EXISTS offsets (
  uniqueID INT PRIMARY KEY,
  creatorId INT NOT NULL,
  hunterId INT,
  worldName VARCHAR(255) NOT NULL,
  logBlocksTarget SMALLINT NOT NULL,
  reward INT NOT NULL,
  timeStarted BIGINT NOT NULL,
  timeCompleted BIGINT
);