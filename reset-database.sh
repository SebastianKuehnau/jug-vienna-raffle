#!/bin/bash
set -e

CONTAINER_NAME="jug-vienna-raffle-postgres"

if ! docker ps | grep -q $CONTAINER_NAME; then
  echo "PostgreSQL container is not running. Starting it..."
  docker-compose up -d postgres
  sleep 5
fi

echo "Clearing database tables instead of dropping the database..."

# First, check if flyway_schema_history table exists
HAS_FLYWAY=$(docker exec -i $CONTAINER_NAME psql -U juguser -d jugviennaraffle -t -c "SELECT EXISTS (SELECT FROM pg_tables WHERE tablename = 'flyway_schema_history');")

if [[ $HAS_FLYWAY =~ t ]]; then
  echo "Truncating flyway_schema_history to allow clean migrations..."
  docker exec -i $CONTAINER_NAME psql -U juguser -d jugviennaraffle -c "TRUNCATE flyway_schema_history;"
fi

# Drop all tables in the public schema (this is safer than dropping the database)
docker exec -i $CONTAINER_NAME psql -U juguser -d jugviennaraffle -c "
DO \$\$ DECLARE
  r RECORD;
BEGIN
  FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
    EXECUTE 'DROP TABLE IF EXISTS ' || r.tablename || ' CASCADE';
  END LOOP;
END \$\$;
"

echo "Database has been reset. You can now start the application with a clean database."