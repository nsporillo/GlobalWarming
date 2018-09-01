CREATE TABLE IF NOT EXISTS contributions (
  uniqueID INT PRIMARY KEY,
  contributerId INT NOT NULL,
  contributionKey INT NOT NULL,
  worldName VARCHAR(255) NOT NULL,
  value SMALLINT NOT NULL
);