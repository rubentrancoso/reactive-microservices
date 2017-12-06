#!/bin/bash
sudo docker stop cassandra
sudo docker rm cassandra
sudo docker run --name cassandra -d -p 9042:9042 cassandra

