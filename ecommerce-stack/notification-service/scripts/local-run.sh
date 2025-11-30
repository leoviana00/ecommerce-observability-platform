#!/bin/bash
mvn clean package -DskipTests
java -jar target/notification-service-1.0.0.jar
