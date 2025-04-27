#!/bin/bash
set -e

echo "Resetting database..."
./reset-database.sh

echo "Starting the application..."
./06_run-mvn-with-postgres.sh