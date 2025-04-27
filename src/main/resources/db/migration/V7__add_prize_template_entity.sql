-- Add new fields to Prize table
ALTER TABLE prize ADD COLUMN IF NOT EXISTS voucher_code VARCHAR(255);
ALTER TABLE prize ADD COLUMN IF NOT EXISTS valid_until DATE;

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