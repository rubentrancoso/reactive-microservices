CREATE COLUMNFAMILY account (
   accountId varchar PRIMARY KEY,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timestamp,
   updated timestamp
);


