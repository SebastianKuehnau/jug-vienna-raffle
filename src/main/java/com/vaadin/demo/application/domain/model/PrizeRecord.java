package com.vaadin.demo.application.domain.model;

/**
 * Immutable domain object representing a prize
 */
public record PrizeRecord(
    Long id,
    String name,
    ParticipantRecord winner,
    RaffleRecord raffle
) {
    /**
     * Create a prize with updated winner
     */
    public PrizeRecord withWinner(ParticipantRecord winner) {
        return new PrizeRecord(
            this.id,
            this.name,
            winner,
            this.raffle
        );
    }
    
    /**
     * Create a simple prize with minimal details 
     */
    public static PrizeRecord simple(Long id, String name) {
        return new PrizeRecord(id, name, null, null);
    }
}