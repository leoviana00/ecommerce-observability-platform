#!/bin/bash
mvn clean package -DskipTests
java -jar target/order-service-1.0.0.jar
