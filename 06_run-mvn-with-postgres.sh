#!/bin/bash

# Start PostgreSQL using docker-compose if it's not already running
if ! docker ps | grep -q jug-vienna-raffle-postgres; then
  echo "Starting PostgreSQL container..."
  docker-compose up -d postgres
  
  # Wait for PostgreSQL to be ready
  echo "Waiting for PostgreSQL to start..."
  until docker exec jug-vienna-raffle-postgres pg_isready -U juguser -d jugviennaraffle; do
    echo "PostgreSQL is unavailable - sleeping"
    sleep 1
  done
  
  echo "PostgreSQL is ready!"
else
  echo "PostgreSQL container is already running."
fi

# Start the application
echo "Starting the application..."
./mvnw spring-boot:run