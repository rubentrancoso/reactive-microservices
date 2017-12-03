CREATE TABLE account(
   accountId text PRIMARY KEY,
   availableCreditLimit double,
   availableWithdrawalLimit double,
   created timeuuid,
   updated timeuuid
);
