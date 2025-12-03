#!/bin/bash
mvn clean package -DskipTests
java -jar target/monitor-health-service-1.0.0.jar
