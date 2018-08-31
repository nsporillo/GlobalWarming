# Note: Unused by the plugin currently
# A TODO exists to read all scripts in for usage
CREATE TABLE IF NOT EXISTS players (
  uniqueID BIGINT PRIMARY KEY,
  uuid VARCHAR(36) NOT NULL,
  firstSeen LONG,
  carbonScore INT
);