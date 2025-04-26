-- This file is used to create the schema for PostgreSQL

-- Create sequences for IDs
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1000 INCREMENT BY 1;

-- The rest of the schema will be managed by Hibernate with ddl-auto=update