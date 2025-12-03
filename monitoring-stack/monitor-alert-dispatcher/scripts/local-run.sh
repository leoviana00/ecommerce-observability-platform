#!/bin/bash
mvn clean package -DskipTests
java -jar target/monitor-alert-dispatcher-1.0.0.jar
