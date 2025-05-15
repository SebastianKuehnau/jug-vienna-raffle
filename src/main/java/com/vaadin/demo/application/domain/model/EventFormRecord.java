package com.vaadin.demo.application.domain.model;

import java.time.OffsetDateTime;

/**
 * Immutable domain object for Event form data
 * Used when specific UI requirements differ from EventRecord
 */
public record EventFormRecord(
    Long id,
    String meetupId,
    String title,
    String description,
    OffsetDateTime dateTime,
    String eventUrl,
    String status
) {
    /**
     * Create a new EventFormRecord from an EventRecord
     */
    public static EventFormRecord fromEventRecord(EventRecord eventRecord) {
        if (eventRecord == null) return null;

        return new EventFormRecord(
            eventRecord.id(),
            eventRecord.meetupId(),
            eventRecord.title(),
            eventRecord.description(),
            eventRecord.eventDate(),
            eventRecord.link(),
            null // status is not in EventRecord
        );
    }

    /**
     * Create a simple form record with minimal details
     */
    public static EventFormRecord simple(Long id, String title) {
        return new EventFormRecord(id, null, title, null, null, null, null);
    }

    /**
     * Create an empty form record
     */
    public static EventFormRecord empty() {
        return new EventFormRecord(null, null, "", "", null, null, null);
    }

    /**
     * Convert to an EventRecord
     */
    public EventRecord toEventRecord() {
        return new EventRecord(
            this.id,
            this.meetupId,
            this.title,
            this.description,
            this.dateTime,
            null, // venue
            this.eventUrl
        );
    }

    /**
     * Create a new form with updated title
     */
    public EventFormRecord withTitle(String title) {
        return new EventFormRecord(this.id, this.meetupId, title, this.description,
                                  this.dateTime, this.eventUrl, this.status);
    }

    /**
     * Create a new form with updated description
     */
    public EventFormRecord withDescription(String description) {
        return new EventFormRecord(this.id, this.meetupId, this.title, description,
                                  this.dateTime, this.eventUrl, this.status);
    }

    /**
     * Create a new form with updated dateTime
     */
    public EventFormRecord withDateTime(OffsetDateTime dateTime) {
        return new EventFormRecord(this.id, this.meetupId, this.title, this.description,
                                  dateTime, this.eventUrl, this.status);
    }

    /**
     * Create a new form with updated eventUrl
     */
    public EventFormRecord withEventUrl(String eventUrl) {
        return new EventFormRecord(this.id, this.meetupId, this.title, this.description,
                                  this.dateTime, eventUrl, this.status);
    }

    /**
     * Create a new form with updated status
     */
    public EventFormRecord withStatus(String status) {
        return new EventFormRecord(this.id, this.meetupId, this.title, this.description,
                                  this.dateTime, this.eventUrl, status);
    }

    /**
     * Create a new form with updated meetupId
     */
    public EventFormRecord withMeetupId(String meetupId) {
        return new EventFormRecord(this.id, meetupId, this.title, this.description,
                                  this.dateTime, this.eventUrl, this.status);
    }
}