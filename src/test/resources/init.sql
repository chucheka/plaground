CREATE TABLE dbo.users (
  id  uniqueidentifier NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  age INT NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
);


INSERT INTO dbo.users (id, username, password, email, age, first_name, last_name)
VALUES (NEWID(), 'Doe', 'john22doe', 'john.doe@example.com', 12, 'Doe', 'John');


