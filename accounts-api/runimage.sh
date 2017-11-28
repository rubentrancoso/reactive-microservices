#!/bin/bash
docker stop challenge
docker rm challenge
docker run --name challenge -p 8080:8080 -p 9001:9001 -d registry.doteva.com/concrete/challenge:0.0.1-SNAPSHOT
