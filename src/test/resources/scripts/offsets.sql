CREATE TABLE IF NOT EXISTS offsets (
  uniqueID BIGINT PRIMARY KEY,
  creatorId BIGINT NOT NULL,
  hunterId BIGINT,
  worldName VARCHAR(255) NOT NULL,
  logBlocksTarget INT NOT NULL,
  reward DOUBLE NOT NULL,
  timeStarted LONG NOT NULL,
  timeCompleted LONG
);