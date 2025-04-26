package com.vaadin.demo.application.domain.port;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;

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
    Optional<Raffle> getRaffleById(Long id);
    
    /**
     * Get a prize by ID
     */
    Optional<Prize> getPrizeById(Long id);
    
    /**
     * Get a raffle by Meetup event ID
     */
    Optional<Raffle> getRaffleByMeetupEventId(String meetupEventId);
    
    /**
     * Get prizes for a raffle
     */
    List<Prize> getPrizesForRaffle(Raffle raffle);
    
    /**
     * Get eligible participants for a raffle
     */
    List<Participant> getEligibleParticipants(Raffle raffle);
    
    /**
     * Create a new raffle for an event
     */
    Raffle createRaffle(MeetupEvent event);
    
    /**
     * Save a raffle
     */
    Raffle saveRaffle(Raffle raffle);
    
    /**
     * Save a prize
     */
    Prize savePrize(Prize prize);
    
    /**
     * Award a prize to a participant
     */
    Prize awardPrize(Prize prize, Participant participant);
    
    /**
     * Delete a prize
     */
    void deletePrize(Long prizeId);
}