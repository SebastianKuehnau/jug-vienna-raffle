package com.vaadin.demo.application.domain.port;

import com.vaadin.demo.application.domain.model.*;

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
     * Get all raffles
     */
    List<RaffleRecord> getAllRaffles();

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
     * Get all prize templates as PrizeTemplateRecord
     */
    List<PrizeTemplateRecord> getAllPrizeTemplateRecords();


    /**
     * Get prize templates by name as PrizeTemplateRecord (partial match)
     */
    List<PrizeTemplateRecord> getPrizeTemplateRecordsByName(String namePattern);



    /**
     * Get a prize template by ID as PrizeTemplateRecord
     */
    Optional<PrizeTemplateRecord> getPrizeTemplateRecordById(Long id);


    /**
     * Create a new prize from a template record
     */
    PrizeRecord createPrizeFromTemplateRecord(Long templateId, RaffleRecord raffle, String voucherCode);

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
     * Save a prize template as PrizeTemplateRecord
     */
    PrizeTemplateRecord savePrizeTemplateRecord(PrizeTemplateRecord prizeTemplate);

    /**
     * Award a prize to a participant
     */
    PrizeRecord awardPrize(PrizeRecord prize, ParticipantRecord participant);

    /**
     * Delete a prize
     */
    void deletePrize(Long prizeId);

    /**
     * Delete a prize template
     */
    void deletePrizeTemplate(Long templateId);
}