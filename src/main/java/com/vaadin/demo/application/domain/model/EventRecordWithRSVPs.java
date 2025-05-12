package com.vaadin.demo.application.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Immutable domain object representing a meetup event
 */

public record EventRecordWithRSVPs(
    Long id,
    String meetupId,
    String title,
    String description,
    OffsetDateTime eventDate,
    String venue,
    String link,
    List<RSVPMember> members
) {
    public record RSVPMember(
        String id, String name, String email, String rsvp_id, Boolean isOrganizer, Boolean hasEnteredRaffle
    ) {}

    /**
     * Create a simple event with minimal details
     */
    public static EventRecordWithRSVPs simple(Long id, String meetupId, String title) {
        return new EventRecordWithRSVPs(
            id,
            meetupId,
            title,
            null,
            null,
            null,
            null,
            List.of()
        );
    }
}