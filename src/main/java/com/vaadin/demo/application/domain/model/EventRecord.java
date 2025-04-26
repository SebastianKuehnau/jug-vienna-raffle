package com.vaadin.demo.application.domain.model;

import java.time.OffsetDateTime;

/**
 * Immutable domain object representing a meetup event
 */
public record EventRecord(
    Long id,
    String meetupId,
    String title,
    String description,
    OffsetDateTime eventDate,
    String venue,
    String link
) {
    /**
     * Create a simple event with minimal details
     */
    public static EventRecord simple(Long id, String meetupId, String title) {
        return new EventRecord(
            id, 
            meetupId, 
            title, 
            null, 
            null, 
            null, 
            null
        );
    }
}