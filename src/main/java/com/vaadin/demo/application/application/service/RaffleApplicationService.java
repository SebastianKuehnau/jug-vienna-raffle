package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.domain.port.RafflePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service that uses the RafflePort
 * This service acts as a façade between the UI layer and the domain layer
 */
@Service
public class RaffleApplicationService {
    
    private final RafflePort rafflePort;
    
    public RaffleApplicationService(RafflePort rafflePort) {
        this.rafflePort = rafflePort;
    }
    
    /**
     * Get a raffle by ID
     */
    public Optional<RaffleRecord> getRaffleById(Long id) {
        return rafflePort.getRaffleById(id);
    }
    
    /**
     * Get a prize by ID
     */
    public Optional<PrizeRecord> getPrizeById(Long id) {
        return rafflePort.getPrizeById(id);
    }
    
    /**
     * Get a raffle by Meetup event ID
     */
    public Optional<RaffleRecord> getRaffleByMeetupEventId(String meetupEventId) {
        return rafflePort.getRaffleByMeetupEventId(meetupEventId);
    }
    
    /**
     * Get prizes for a raffle
     */
    public List<PrizeRecord> getPrizesForRaffle(RaffleRecord raffle) {
        return rafflePort.getPrizesForRaffle(raffle);
    }
    
    /**
     * Get eligible participants for a raffle
     */
    public List<ParticipantRecord> getEligibleParticipants(RaffleRecord raffle) {
        return rafflePort.getEligibleParticipants(raffle);
    }
    
    /**
     * Create a new raffle for an event
     */
    public RaffleRecord createRaffle(EventRecord event) {
        return rafflePort.createRaffle(event);
    }
    
    /**
     * Save a raffle
     */
    public RaffleRecord saveRaffle(RaffleRecord raffle) {
        return rafflePort.saveRaffle(raffle);
    }
    
    /**
     * Save a prize
     */
    public PrizeRecord savePrize(PrizeRecord prize) {
        return rafflePort.savePrize(prize);
    }
    
    /**
     * Award a prize to a participant
     */
    public PrizeRecord awardPrize(PrizeRecord prize, ParticipantRecord participant) {
        return rafflePort.awardPrize(prize, participant);
    }
    
    /**
     * Delete a prize
     */
    public void deletePrize(Long prizeId) {
        rafflePort.deletePrize(prizeId);
    }
}