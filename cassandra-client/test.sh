#!/bin/bash
./gradlew clean test
##  --debug
google-chrome file:///$(pwd)/build/reports/tests/test/index.html
