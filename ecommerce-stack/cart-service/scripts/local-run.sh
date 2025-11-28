#!/bin/bash
mvn clean package -DskipTests
java -jar target/cart-service-1.0.0.jar
