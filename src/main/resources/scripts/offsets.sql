# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS offsets (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  creatorId VARCHAR(36) NOT NULL,
  hunterId VARCHAR(36),
  worldName VARCHAR(36) NOT NULL,
  logBlocksTarget INT,
  reward DOUBLE,
  timeStarted LONG,
  timeCompleted LONG
);