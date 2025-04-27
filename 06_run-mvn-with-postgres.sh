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

# Run the fix-flyway script to ensure the database schema is properly configured
echo "Running Flyway fix script..."
./fix-flyway.sh

# Start the application
echo "Starting the application..."
# Run with flyway.baseline-on-migrate=true
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.flyway.baseline-on-migrate=true"