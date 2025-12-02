#!/bin/bash
mvn clean package -DskipTests
java -jar target/monitor-state-manager-1.0.0.jar
