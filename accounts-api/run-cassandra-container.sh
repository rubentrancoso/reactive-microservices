#!/bin/bash
sudo docker stop cassandra
sudo docker rm cassandra
sudo docker run --name cassandra -d -p 9042:9042 cassandra
sudo docker exec -it cassandra /bin/bash
# cqlsh
# create keyspace accounts  with replication={'class':'SimpleStrategy', 'replication_factor':1};
# describe keyspaces
# use accounts

# CREATE TABLE account(
#    accountId text PRIMARY KEY,
#    availableCreditLimit double,
#    availableWithdrawalLimit double
# );

# drop table account
