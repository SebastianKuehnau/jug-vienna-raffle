package com.vaadin.demo.application.domain.model;

import java.util.List;

/**
 * Immutable domain object for Raffle form data
 * Used when specific UI requirements differ from RaffleRecord
 */
public record RaffleFormRecord(
    Long id,
    String meetupEventId,
    String eventTitle,
    List<String> prizeNames
) {
    /**
     * Create a new RaffleFormRecord from a RaffleRecord
     */
    public static RaffleFormRecord fromRaffleRecord(RaffleRecord raffleRecord) {
        if (raffleRecord == null) return null;
        
        List<String> prizeNames = raffleRecord.prizes() != null ?
            raffleRecord.prizes().stream()
                .map(PrizeRecord::name)
                .toList() :
            List.of();
        
        return new RaffleFormRecord(
            raffleRecord.id(),
            raffleRecord.meetupEventId(),
            raffleRecord.event() != null ? raffleRecord.event().title() : null,
            prizeNames
        );
    }
    
    /**
     * Create a simple form record with minimal details
     */
    public static RaffleFormRecord simple(Long id, String meetupEventId) {
        return new RaffleFormRecord(id, meetupEventId, null, List.of());
    }
    
    /**
     * Create an empty form record
     */
    public static RaffleFormRecord empty() {
        return new RaffleFormRecord(null, "", "", List.of());
    }
    
    /**
     * Create a new form with updated meetupEventId
     */
    public RaffleFormRecord withMeetupEventId(String meetupEventId) {
        return new RaffleFormRecord(this.id, meetupEventId, this.eventTitle, this.prizeNames);
    }
    
    /**
     * Create a new form with updated eventTitle
     */
    public RaffleFormRecord withEventTitle(String eventTitle) {
        return new RaffleFormRecord(this.id, this.meetupEventId, eventTitle, this.prizeNames);
    }
    
    /**
     * Create a new form with updated prizeNames
     */
    public RaffleFormRecord withPrizeNames(List<String> prizeNames) {
        return new RaffleFormRecord(this.id, this.meetupEventId, this.eventTitle, prizeNames);
    }
}