-- V1: Initial schema

-- Create sequence for hibernate
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS idgenerator START WITH 1000 INCREMENT BY 50;

-- MeetupEvent table
CREATE TABLE IF NOT EXISTS meetup_event (
                                            id BIGINT PRIMARY KEY,
                                            version INTEGER NOT NULL,
                                            meetup_id VARCHAR(255) UNIQUE,
    token VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    date_time TIMESTAMP WITH TIME ZONE,
    event_url VARCHAR(500),
    status VARCHAR(50),
    last_updated TIMESTAMP WITH TIME ZONE
                            );

-- Create Member table
CREATE TABLE IF NOT EXISTS member (
                                      id BIGINT PRIMARY KEY,
                                      version INTEGER NOT NULL,
                                      meetup_id VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255),
    last_updated TIMESTAMP WITH TIME ZONE
                               );

-- Create indexes for Member
CREATE INDEX idx_member_meetup_id ON member(meetup_id);

-- MeetupMember table
CREATE TABLE IF NOT EXISTS meetup_member (
                                             id BIGINT PRIMARY KEY,
                                             version INTEGER NOT NULL,
                                             meetup_event_id BIGINT,
                                             meetup_id VARCHAR(255),
    name VARCHAR(255),
    email VARCHAR(255),
    rsvp_id VARCHAR(255),
    is_organizer BOOLEAN DEFAULT FALSE,
    has_entered_raffle BOOLEAN DEFAULT FALSE,
    rsvp_status VARCHAR(3) DEFAULT 'YES',  -- Only YES or NO
    attendance_status VARCHAR(20) DEFAULT 'UNKNOWN',  -- UNKNOWN, ATTENDED, NO_SHOW
    last_updated TIMESTAMP WITH TIME ZONE,
                               FOREIGN KEY (meetup_event_id) REFERENCES meetup_event(id),
    UNIQUE (meetup_event_id, meetup_id)
    );

-- Add index for faster lookups
CREATE INDEX idx_meetup_member_meetup_id ON meetup_member(meetup_id);
CREATE INDEX idx_meetup_member_rsvp_status ON meetup_member(rsvp_status);
CREATE INDEX idx_meetup_member_attendance ON meetup_member(attendance_status);


-- Create Participant table (it doesn't exist yet)
CREATE TABLE IF NOT EXISTS participant (
                                           id BIGINT PRIMARY KEY,
                                           version INTEGER NOT NULL,
                                           meetup_event_id BIGINT,
                                           member_id BIGINT,
                                           rsvp_id VARCHAR(255),
    is_organizer BOOLEAN DEFAULT FALSE,
    has_entered_raffle BOOLEAN DEFAULT FALSE,
    rsvp_status VARCHAR(3) DEFAULT 'YES',
    attendance_status VARCHAR(20) DEFAULT 'UNKNOWN',
    last_updated TIMESTAMP WITH TIME ZONE
                               );

-- Add foreign key constraints
ALTER TABLE participant ADD CONSTRAINT fk_participant_meetup_event
    FOREIGN KEY (meetup_event_id) REFERENCES meetup_event(id);
ALTER TABLE participant ADD CONSTRAINT fk_participant_member
    FOREIGN KEY (member_id) REFERENCES member(id);
-- Add unique constraint
ALTER TABLE participant ADD CONSTRAINT uk_participant_event_member
    UNIQUE (meetup_event_id, member_id);


-- Add indexes for faster lookups
CREATE INDEX idx_participant_meetup_event ON participant(meetup_event_id);
CREATE INDEX idx_participant_member ON participant(member_id);
CREATE INDEX idx_participant_rsvp_status ON participant(rsvp_status);
CREATE INDEX idx_participant_attendance ON participant(attendance_status);

-- Raffle table
CREATE TABLE IF NOT EXISTS raffle (
                                      id BIGINT PRIMARY KEY,
                                      version INTEGER NOT NULL,
                                      meetup_event_id VARCHAR(255),
    event_id BIGINT
    );

-- Add foreign key constraint for Raffle.event_id
ALTER TABLE raffle ADD CONSTRAINT fk_raffle_event
    FOREIGN KEY (event_id) REFERENCES meetup_event(id);


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


-- Create Prize Template table
CREATE TABLE IF NOT EXISTS prize_template (
    id BIGINT NOT NULL,
    name VARCHAR(255),
    description VARCHAR(2000),
    template_text VARCHAR(4000),
    voucher_code VARCHAR(255),
    valid_until DATE,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
    );

-- Add sequence for Prize Template if not exists
CREATE SEQUENCE IF NOT EXISTS prize_template_seq
  START WITH 1
  INCREMENT BY 50
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;



-- Prize table
CREATE TABLE IF NOT EXISTS prize (
    id BIGINT PRIMARY KEY,
    version INTEGER NOT NULL,
    name VARCHAR(255),
    winner_name VARCHAR(255),
    raffle_id BIGINT,
    winner_id BIGINT,
    description varchar(2000),
    template_text varchar(4000),
    voucher_code varchar(255),
    valid_until date,
    FOREIGN KEY (raffle_id) REFERENCES raffle(id)
);

-- Update prize foreign key to point to participant
ALTER TABLE prize ADD CONSTRAINT fk_prize_winner
    FOREIGN KEY (winner_id) REFERENCES participant(id);