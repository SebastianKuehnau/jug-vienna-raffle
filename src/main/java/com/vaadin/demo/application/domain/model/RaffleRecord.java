package com.vaadin.demo.application.domain.model;

import java.util.List;

/**
 * Immutable domain object representing a raffle
 */
public record RaffleRecord(
    Long id,
    EventRecord event,
    List<PrizeRecord> prizes
) {
    /**
     * Create a simple raffle with minimal details
     */
    public static RaffleRecord simple(Long id, EventRecord event) {
        return new RaffleRecord(id, event, List.of());
    }
}