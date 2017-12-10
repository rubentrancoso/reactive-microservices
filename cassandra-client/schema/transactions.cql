CREATE COLUMNFAMILY transaction (
   transactionId varchar,
   accountId varchar, 
   operationTypeId int,
   chargeOrder int,
   amount double,
   balance double,
   eventDate timestamp,
   dueDate timestamp,
   PRIMARY KEY( accountId, eventDate, chargeOrder, operationTypeId)
);

CREATE MATERIALIZED VIEW transactionbychargeorder AS
SELECT * FROM transaction
  WHERE 
  	chargeOrder IS NOT NULL AND
  	eventDate IS NOT NULL AND
  	operationTypeId IS NOT NULL
PRIMARY KEY(accountId, chargeOrder, eventDate, operationTypeId)
WITH CLUSTERING ORDER BY (chargeOrder ASC, eventDate ASC);

CREATE MATERIALIZED VIEW payment AS
SELECT * FROM transaction
  WHERE 
  	chargeOrder IS NOT NULL AND
  	eventDate IS NOT NULL AND
  	operationTypeId=4
PRIMARY KEY(accountId, eventDate, operationTypeId, chargeOrder)
WITH CLUSTERING ORDER BY (eventDate ASC, operationTypeId DESC);

CREATE COLUMNFAMILY paymenttracking (
   paymentTrackintId varchar PRIMARY KEY,
   amount double,
   creditTransactionId varchar,
   debitTransactionId varchar,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timestamp
);


