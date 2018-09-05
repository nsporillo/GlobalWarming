CREATE TABLE IF NOT EXISTS furnaces (
  uniqueID INT PRIMARY KEY,
  ownerID INT NOT NULL,
  worldName VARCHAR(255) NOT NULL,
  blockX INT NOT NULL,
  blockY INT NOT NULL,
  blockZ INT NOT NULL,
  active BOOL NOT NULL
);