CREATE TABLE IF NOT EXISTS furnaces (
  uniqueId INT PRIMARY KEY,
  ownerId INT NOT NULL,
  worldId VARCHAR(36) NOT NULL,
  blockX INT NOT NULL,
  blockY INT NOT NULL,
  blockZ INT NOT NULL,
  active BOOL NOT NULL
);