# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS reductions (
  uniqueID VARCHAR(36) NOT NULL,
  reductionerId VARCHAR(36),
  reductionKey VARCHAR(36),
  worldName VARCHAR(255),
  value DOUBLE,
  PRIMARY KEY (uniqueID)
);