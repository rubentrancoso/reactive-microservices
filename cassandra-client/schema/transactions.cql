CREATE TABLE transaction (
   transactionId text PRIMARY KEY,
   accountId text, 
   operationTypeId int,
   amount double,
   balance double,
   eventDate timestamp,
   dueDate timestamp
);

CREATE TABLE payment (
   paymentTrackintId text PRIMARY KEY,
   amount double,
   creditTransactionId text,
   debitTransactionId text,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timestamp
);

