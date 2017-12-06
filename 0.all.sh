#!/bin/bash
./1.cassandra.sh
sleep 15
./2.build.sh
./3.client.sh
./4.build.sh
./5.accounts.sh

