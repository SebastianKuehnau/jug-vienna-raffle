-- Create some example prize templates if they don't exist already
DO $$
BEGIN
    -- Only insert if there are no templates yet
    IF NOT EXISTS (SELECT 1 FROM prize_template ) THEN
        -- IntelliJ IDEA License template
        INSERT INTO prize_template (id, version, name, description, template_text)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'IntelliJ IDEA License',
            'JetBrains IntelliJ IDEA Ultimate license valid for 1 year',
            'Hello {{WINNER_NAME}},

Congratulations on winning the "{{PRIZE_NAME}}" at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your prize, please go to https://jetbrains.com/redeem and enter the following code:

{{VOUCHER_CODE}}

This code is valid for 3 months. Enjoy your new IDE!

Best regards,
JUG Vienna Team'
        );

        -- Conference Ticket template
        INSERT INTO prize_template (id, version, name, description, template_text)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'Conference Ticket',
            'Free conference ticket to a tech conference',
            'Hello {{WINNER_NAME}},

Congratulations on winning a ticket to the conference at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your ticket, please use the following registration code:

{{VOUCHER_CODE}}

Please register using this code before the deadline.

Best regards,
JUG Vienna Team'
        );

        -- Book Voucher template
        INSERT INTO prize_template (id, version, name, description, template_text)
        VALUES (
            nextval('hibernate_sequence'),
            0,
            'Book Voucher',
            'Book voucher for technical books',
            'Hello {{WINNER_NAME}},

Congratulations on winning a "{{PRIZE_NAME}}" at our JUG Vienna raffle on {{RAFFLE_DATE}}!

To redeem your book voucher, please use the following code at checkout:

{{VOUCHER_CODE}}

This voucher is valid for 6 months.

Happy reading!
JUG Vienna Team'
        );
    END IF;
END
$$;