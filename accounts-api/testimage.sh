#!/bin/bash
./build.sh
./buildimage.sh
./runimage.sh
echo -e "waiting image to be ready"
sleep 10
google-chrome http://localhost:9001/info
