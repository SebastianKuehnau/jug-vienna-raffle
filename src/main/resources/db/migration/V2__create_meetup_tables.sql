-- V2: Create Meetup tables

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