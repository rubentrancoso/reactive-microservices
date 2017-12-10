CREATE COLUMNFAMILY transaction (
   transactionId varchar,
   accountId varchar, 
   operationTypeId int,
   chargeOrder int,
   amount double,
   balance double,
   eventDate timestamp,
   dueDate timestamp,
   PRIMARY KEY( accountId, eventDate, chargeOrder)
);

CREATE MATERIALIZED VIEW transactionorder AS
SELECT * FROM transaction
  WHERE 
    accountId IS NOT NULL AND
  	transactionId IS NOT NULL AND 
  	operationTypeId IS NOT NULL AND
  	chargeOrder IS NOT NULL AND
  	eventDate IS NOT NULL
PRIMARY KEY(accountId, chargeOrder, eventDate, operationtypeid)
WITH CLUSTERING ORDER BY (chargeOrder ASC, eventDate ASC);

CREATE INDEX chargeOrder ON transaction (chargeOrder);

CREATE COLUMNFAMILY paymenttracking (
   paymentTrackintId varchar PRIMARY KEY,
   amount double,
   creditTransactionId varchar,
   debitTransactionId varchar,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timestamp
);


