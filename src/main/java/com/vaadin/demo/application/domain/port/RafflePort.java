package com.vaadin.demo.application.domain.port;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;

import java.util.List;
import java.util.Optional;

/**
 * Port for Raffle service operations
 * This defines the interface for interacting with Raffle-related functionality
 */
public interface RafflePort {
    
    /**
     * Get a raffle by ID
     */
    Optional<RaffleRecord> getRaffleById(Long id);
    
    /**
     * Get a prize by ID
     */
    Optional<PrizeRecord> getPrizeById(Long id);
    
    /**
     * Get a raffle by Meetup event ID
     */
    Optional<RaffleRecord> getRaffleByMeetupEventId(String meetupEventId);
    
    /**
     * Get prizes for a raffle
     */
    List<PrizeRecord> getPrizesForRaffle(RaffleRecord raffle);
    
    /**
     * Get eligible participants for a raffle
     */
    List<ParticipantRecord> getEligibleParticipants(RaffleRecord raffle);
    
    /**
     * Create a new raffle for an event
     */
    RaffleRecord createRaffle(EventRecord event);
    
    /**
     * Save a raffle
     */
    RaffleRecord saveRaffle(RaffleRecord raffle);
    
    /**
     * Save a prize
     */
    PrizeRecord savePrize(PrizeRecord prize);
    
    /**
     * Award a prize to a participant
     */
    PrizeRecord awardPrize(PrizeRecord prize, ParticipantRecord participant);
    
    /**
     * Delete a prize
     */
    void deletePrize(Long prizeId);
}