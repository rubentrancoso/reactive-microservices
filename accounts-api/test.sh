#!/bin/bash -x

# junit 4 test

sudo docker stop cassandra
sudo docker rm cassandra
sudo docker run --name cassandra -d -p 9042:9042 cassandra

sleep 15

./gradlew clean test
# --debug

# junit 5
# gradle junitPlatformTest

sudo docker stop cassandra
sudo docker rm cassandra

google-chrome file:///$(pwd)/build/reports/tests/test/index.html
