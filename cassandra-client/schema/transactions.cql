CREATE TABLE transaction (
   transactionId text,
   accountId text, 
   operationTypeId int,
   chargeOrder int,
   amount double,
   balance double,
   eventDate timestamp,
   dueDate timestamp,
   PRIMARY KEY(accountId, chargeOrder, eventDate, transactionId)
);

CREATE TABLE paymenttracking (
   paymentTrackintId text PRIMARY KEY,
   amount double,
   creditTransactionId text,
   debitTransactionId text,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timestamp
);

