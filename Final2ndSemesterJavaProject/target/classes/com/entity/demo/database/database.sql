CREATE DATABASE ChatApplicationDB;
USE ChatApplicationDB;
CREATE TABLE Accounts (
	userId NVARCHAR(225) NOT NULL,
    userPassword NVARCHAR(225) NOT NULL
);
ALTER TABLE Accounts ADD CONSTRAINT PK_Accounts PRIMARY KEY (userId);
CREATE TABLE PublicMessage (
	sender NVARCHAR(225) NOT NULL,
    message NVARCHAR(225) NOT NULL,
    sendTime DATETIME NOT NULL
);
ALTER TABLE PublicMessage ADD CONSTRAINT PK_PublicMessage PRIMARY KEY (sender, sendTime);
CREATE TABLE Contacts (
	userId1 VARCHAR(225) NOT NULL,
    userId2 VARCHAR(225) NOT NULL,
    relationId VARCHAR(225) NOT NULL,
    PRIMARY KEY (relationId),
    FOREIGN KEY (userId1)
		REFERENCES Accounts(userId)
        ON DELETE CASCADE,
	FOREIGN KEY (userId2)
		REFERENCES Accounts(userId)
        ON DELETE CASCADE
);
CREATE TABLE DirectMessage (
	contactId VARCHAR(225) NOT NULL,
    sender VARCHAR(225) NOT NULL,
    sendTime DATETIME NOT NULL,
    message NVARCHAR(225) NOT NULL,
    PRIMARY KEY (contactId, sender, sendTime),
    FOREIGN KEY (contactId)
		REFERENCES Contacts(relationId)
        ON DELETE CASCADE
);
CREATE TABLE FriendRequests (
	userId1 VARCHAR(225) NOT NULL,
    userId2 VARCHAR(225) NOT NULL,
    relationId VARCHAR(225) NOT NULL,
    PRIMARY KEY (relationId),
    FOREIGN KEY (userId1)
		REFERENCES Accounts(userId)
        ON DELETE CASCADE,
	FOREIGN KEY (userId2)
		REFERENCES Accounts(userId)
        ON DELETE CASCADE
);