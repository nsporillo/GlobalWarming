# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS worlds (
  uniqueID BIGINT PRIMARY KEY,
  worldName VARCHAR(255) NOT NULL,
  firstSeen LONG,
  carbonValue INT,
  seaLevel INT,
  size INT
);