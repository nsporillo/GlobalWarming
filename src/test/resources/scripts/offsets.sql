# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS offsets (
  uniqueID BIGINT PRIMARY KEY,
  creatorId BIGINT NOT NULL,
  hunterId BIGINT,
  worldName VARCHAR(36) NOT NULL,
  logBlocksTarget INT,
  reward DOUBLE,
  timeStarted LONG,
  timeCompleted LONG
);