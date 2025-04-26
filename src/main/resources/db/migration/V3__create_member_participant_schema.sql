-- V3: Restructure data model to separate members from events
-- Create Member table to store members independent of events

-- First rename columns in Prize table to prepare for the change
ALTER TABLE prize RENAME COLUMN winner TO winner_name;
ALTER TABLE prize ADD COLUMN winner_id BIGINT;

-- Add event_id to Raffle table
ALTER TABLE raffle ADD COLUMN event_id BIGINT;

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

-- Add foreign key constraint for Raffle.event_id
ALTER TABLE raffle ADD CONSTRAINT fk_raffle_event
    FOREIGN KEY (event_id) REFERENCES meetup_event(id);

-- Update prize foreign key to point to participant
ALTER TABLE prize ADD CONSTRAINT fk_prize_winner
    FOREIGN KEY (winner_id) REFERENCES participant(id);