-- V1: Initial schema

-- Create sequence for hibernate
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1000 INCREMENT BY 1;

-- Sample Person table
CREATE TABLE IF NOT EXISTS sample_person (
    id BIGINT PRIMARY KEY,
    version INTEGER NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    date_of_birth DATE,
    occupation VARCHAR(255),
    role VARCHAR(255),
    important BOOLEAN
);

-- Application User table
CREATE TABLE IF NOT EXISTS application_user (
    id BIGINT PRIMARY KEY,
    version INTEGER NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    hashed_password VARCHAR(255) NOT NULL,
    profile_picture BYTEA
);

-- User Roles table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(255),
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES application_user(id)
);

-- Raffle table
CREATE TABLE IF NOT EXISTS raffle (
    id BIGINT PRIMARY KEY,
    version INTEGER NOT NULL,
    meetup_event_id VARCHAR(255)
);

-- Prize table
CREATE TABLE IF NOT EXISTS prize (
    id BIGINT PRIMARY KEY,
    version INTEGER NOT NULL,
    name VARCHAR(255),
    winner VARCHAR(255),
    raffle_id BIGINT,
    FOREIGN KEY (raffle_id) REFERENCES raffle(id)
);