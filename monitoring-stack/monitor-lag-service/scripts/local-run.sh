#!/bin/bash
mvn clean package -DskipTests
java -jar target/monitor-lag-service-1.0.0.jar
