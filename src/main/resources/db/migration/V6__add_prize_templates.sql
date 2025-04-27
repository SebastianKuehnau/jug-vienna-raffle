-- This script has been modified to handle the case where the columns already exist

-- Conditionally add columns only if they don't exist
DO $$
BEGIN
    -- Check if description column exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'prize' AND column_name = 'description'
    ) THEN
        ALTER TABLE prize ADD COLUMN description VARCHAR(2000);
    END IF;
    
    -- Check if template column exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'prize' AND column_name = 'template'
    ) THEN
        ALTER TABLE prize ADD COLUMN template BOOLEAN DEFAULT FALSE;
    END IF;
    
    -- Check if template_text column exists
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'prize' AND column_name = 'template_text'
    ) THEN
        ALTER TABLE prize ADD COLUMN template_text VARCHAR(4000);
    END IF;
END
$$;

-- Create some example prize templates if they don't exist already
DO $$
BEGIN
    -- Only insert if there are no templates yet
    IF NOT EXISTS (SELECT 1 FROM prize WHERE template = TRUE) THEN
        -- IntelliJ IDEA License template
        INSERT INTO prize (id, version, name, description, template, template_text, winner_id, raffle_id, winner_name)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'IntelliJ IDEA License', 
            'JetBrains IntelliJ IDEA Ultimate license valid for 1 year',
            TRUE,
            'Hello {{WINNER_NAME}},

Congratulations on winning the "{{PRIZE_NAME}}" at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your prize, please go to https://jetbrains.com/redeem and enter the following code:

{{VOUCHER_CODE}}

This code is valid for 3 months. Enjoy your new IDE!

Best regards,
JUG Vienna Team',
            NULL,
            NULL,
            NULL
        );

        -- Conference Ticket template
        INSERT INTO prize (id, version, name, description, template, template_text, winner_id, raffle_id, winner_name)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'Conference Ticket', 
            'Free conference ticket to a tech conference',
            TRUE,
            'Hello {{WINNER_NAME}},

Congratulations on winning a ticket to the conference at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your ticket, please use the following registration code:

{{VOUCHER_CODE}}

Please register using this code before the deadline.

Best regards,
JUG Vienna Team',
            NULL,
            NULL,
            NULL
        );

        -- Book Voucher template
        INSERT INTO prize (id, version, name, description, template, template_text, winner_id, raffle_id, winner_name)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'Book Voucher', 
            'Book voucher for technical books',
            TRUE,
            'Hello {{WINNER_NAME}},

Congratulations on winning a "{{PRIZE_NAME}}" at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your book voucher, please use the following code at checkout:

{{VOUCHER_CODE}}

This voucher is valid for 6 months.

Happy reading!
JUG Vienna Team',
            NULL,
            NULL,
            NULL
        );
    END IF;
END
$$;