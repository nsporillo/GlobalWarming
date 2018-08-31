# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS reductions (
  uniqueID BIGINT PRIMARY KEY,
  reductionerId BIGINT,
  reductionKey BIGINT,
  worldName VARCHAR(255),
  value DOUBLE
);