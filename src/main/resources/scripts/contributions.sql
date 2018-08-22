# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS contributions (
  uniqueID VARCHAR(36) NOT NULL,
  contributerId VARCHAR(36),
  contributionKey VARCHAR(36),
  worldName VARCHAR(255),
  value DOUBLE,
  PRIMARY KEY (uniqueID)
);