-- V4: Add idgenerator sequence needed by Hibernate for entity ID generation

-- Create the idgenerator sequence
CREATE SEQUENCE IF NOT EXISTS idgenerator START WITH 1000 INCREMENT BY 50;