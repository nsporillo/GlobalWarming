# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS trees (
  uniqueID BIGINT PRIMARY KEY,
  ownerUUID BIGINT,
  worldName VARCHAR(255),
  blockX INT,
  blockY INT,
  blockZ INT,
  sapling BOOL,
  size INT
);