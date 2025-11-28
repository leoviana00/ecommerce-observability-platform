#!/bin/bash
mvn clean package -DskipTests
java -jar target/payment-service-1.0.0.jar
