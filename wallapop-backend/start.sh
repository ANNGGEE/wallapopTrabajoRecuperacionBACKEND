#!/bin/bash
# start.sh - Arranca tu backend Spring Boot en Railway

# Paso 1: Construye la app con Maven (genera el .jar)
./mvnw clean package

# Paso 2: Ejecuta la app usando el puerto que da Railway
# "target/wallapop-backend-0.0.1-SNAPSHOT.jar" es el .jar que genera Maven
java -jar target/wallapop-backend-0.0.1-SNAPSHOT.jar
