# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS furnaces (
  uniqueID VARCHAR(36) NOT NULL,
  ownerUUID VARCHAR(36),
  worldName VARCHAR(255),
  blockX INT,
  blockY INT,
  blockZ INT,
  sapling BOOL,
  size INT,
  PRIMARY KEY (uniqueID)
);