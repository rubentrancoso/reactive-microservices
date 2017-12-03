#!/bin/bash -x
./gradlew clean
./gradlew build "$@"
