#!/bin/bash
./run-cassandra-container.sh
sleep 15
java -jar build/libs/cassandra-client-0.0.1-SNAPSHOT.jar
