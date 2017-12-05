#!/bin/bash -x
java -version

./gradlew clean
./gradlew build "$@"
