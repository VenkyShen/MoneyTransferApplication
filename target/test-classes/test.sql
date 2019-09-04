DROP TABLE IF EXISTS User;

CREATE TABLE User (id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 name VARCHAR(30) NOT NULL, mail VARCHAR(30) NOT NULL,
 created_at date, updated_at date);

CREATE UNIQUE INDEX idx_ue on User(name,mail);

INSERT INTO User (id, name, mail, created_at, updated_at) VALUES (1, 'Root', 'Root@gmail.com', now(), now());
INSERT INTO User (id, name, mail, created_at, updated_at) VALUES (2, 'Smith', 'Amla@gmail.com', now(), now());
INSERT INTO User (id, name, mail, created_at, updated_at) VALUES (3, 'Kohli', 'kohli@gmail.com', now(), now());

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
acc_type VARCHAR(30), user_id LONG, balance DECIMAL(19,4),
currency_type VARCHAR(30), status VARCHAR(30), 
created_at date, updated_at date);

INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (1, 'SAVINGS', 1, 100.0000,'EURO', 'OPEN', now(), now());
INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (2, 'CURRENT', 1, 200.0000,'EURO', 'OPEN', now(), now());
INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (3, 'SAVINGS', 2, 150.0000,'DOLLAR', 'OPEN', now(), now());
INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (4, 'CURRENT', 2, 180.0000,'DOLLAR', 'OPEN', now(), now());
INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (5, 'SAVINGS', 3, 1000.0000,'INR', 'OPEN', now(), now());
INSERT INTO Account (id, acc_type, user_id, balance,currency_type, status, created_at, updated_at) VALUES (6, 'CURRENT', 3, 1700.0000,'INR', 'OPEN', now(), now());


DROP TABLE IF EXISTS transaction;

CREATE TABLE transaction (id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
from_account LONG, to_account LONG, amount DECIMAL(19,4), currency_type VARCHAR(30), 
status VARCHAR(30), created_at date, updated_at date);

INSERT INTO transaction (id, from_account, to_account, amount, currency_type, status, created_at, updated_at) VALUES (1, 1, 2, 10, 'DOLLAR', 'INITIATED', now(), now());
INSERT INTO transaction (id, from_account, to_account, amount, currency_type, status, created_at, updated_at) VALUES (2, 2, 1, 10, 'DOLLAR', 'INITIATED', now(), now());
INSERT INTO transaction (id, from_account, to_account, amount, currency_type, status, created_at, updated_at) VALUES (3, 3, 2, 10, 'DOLLAR', 'INITIATED', now(), now());