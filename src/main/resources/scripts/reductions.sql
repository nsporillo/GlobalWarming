CREATE TABLE IF NOT EXISTS reductions (
  uniqueID INT PRIMARY KEY,
  reductionerId INT NOT NULL,
  reductionKey INT NOT NULL,
  worldName VARCHAR(255) NOT NULL,
  value SMALLINT NOT NULL
);