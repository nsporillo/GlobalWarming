# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS worlds (
  worldName VARCHAR(36) NOT NULL,
  firstSeen LONG,
  carbonValue INT,
  size INT,
  PRIMARY KEY (worldName)
);