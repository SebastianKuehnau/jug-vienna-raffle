package com.vaadin.demo.application.domain.model;

import java.util.List;

/**
 * Immutable domain object representing a raffle
 */
public record RaffleRecord(
    Long id,
    EventRecord event,
    String meetupId,
    List<PrizeRecord> prizes
) {
    /**
     * Create a simple raffle with minimal details
     */
    public static RaffleRecord simple(Long id, EventRecord event, String meetupId) {
        return new RaffleRecord(id, event, meetupId, List.of());
    }
    
    /**
     * Get the Meetup event ID
     * This is for compatibility with the current implementation
     */
    public String meetupEventId() {
        return this.meetupId;
    }
}