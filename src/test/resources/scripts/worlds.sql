CREATE TABLE IF NOT EXISTS worlds (
  uniqueID BIGINT PRIMARY KEY,
  worldName VARCHAR(255) NOT NULL,
  firstSeen LONG NOT NULL,
  carbonValue INT NOT NULL,
  seaLevel INT NOT NULL,
  size INT
);