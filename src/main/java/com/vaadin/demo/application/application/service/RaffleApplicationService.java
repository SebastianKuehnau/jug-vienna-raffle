package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.domain.port.RafflePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Get all prize templates
     * @deprecated Use {@link #getAllPrizeTemplateRecords()} instead
     */
    @Deprecated
    public List<PrizeRecord> getAllPrizeTemplates() {
        return rafflePort.getAllPrizeTemplates();
    }
    
    /**
     * Get all prize templates as PrizeTemplateRecord objects
     */
    public List<PrizeTemplateRecord> getAllPrizeTemplateRecords() {
        return rafflePort.getAllPrizeTemplateRecords();
    }
    
    /**
     * Get prize templates by name (partial match)
     * @deprecated Use {@link #getPrizeTemplateRecordsByName(String)} instead
     */
    @Deprecated
    public List<PrizeRecord> getPrizeTemplatesByName(String namePattern) {
        return rafflePort.getPrizeTemplatesByName(namePattern);
    }
    
    /**
     * Get prize templates by name as PrizeTemplateRecord objects (partial match)
     */
    public List<PrizeTemplateRecord> getPrizeTemplateRecordsByName(String namePattern) {
        return rafflePort.getPrizeTemplateRecordsByName(namePattern);
    }
    
    /**
     * Get a prize template by ID
     * @deprecated Use {@link #getPrizeTemplateRecordById(Long)} instead
     */
    @Deprecated
    public Optional<PrizeRecord> getPrizeTemplateById(Long id) {
        return rafflePort.getPrizeTemplateById(id);
    }
    
    /**
     * Get a prize template by ID as PrizeTemplateRecord
     */
    public Optional<PrizeTemplateRecord> getPrizeTemplateRecordById(Long id) {
        return rafflePort.getPrizeTemplateRecordById(id);
    }
    
    /**
     * Create a new prize from a template
     * @deprecated Use {@link #createPrizeFromTemplateRecord(Long, RaffleRecord, String)} instead
     */
    @Deprecated
    public PrizeRecord createPrizeFromTemplate(Long templateId, RaffleRecord raffle, String voucherCode) {
        return rafflePort.createPrizeFromTemplate(templateId, raffle, voucherCode);
    }
    
    /**
     * Create a new prize from a template record
     */
    public PrizeRecord createPrizeFromTemplateRecord(Long templateId, RaffleRecord raffle, String voucherCode) {
        return rafflePort.createPrizeFromTemplateRecord(templateId, raffle, voucherCode);
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
     * Save a prize form
     * This converts the form data to a domain record and saves it
     */
    public PrizeFormRecord savePrizeForm(PrizeFormRecord prizeForm, RaffleRecord raffle) {
        PrizeRecord prizeRecord = prizeForm.toPrizeRecord(raffle);
        PrizeRecord savedPrize = rafflePort.savePrize(prizeRecord);
        return PrizeFormRecord.fromPrizeRecord(savedPrize);
    }
    
    /**
     * Get a prize form by ID
     */
    public Optional<PrizeFormRecord> getPrizeFormById(Long id) {
        return rafflePort.getPrizeById(id)
                .map(PrizeFormRecord::fromPrizeRecord);
    }
    
    
    /**
     * Save a prize template
     * @deprecated Use {@link #savePrizeTemplateRecord(PrizeTemplateRecord)} instead
     */
    @Deprecated
    public PrizeRecord savePrizeTemplate(PrizeRecord prizeTemplate) {
        return rafflePort.savePrizeTemplate(prizeTemplate);
    }
    
    /**
     * Save a prize template record
     */
    public PrizeTemplateRecord savePrizeTemplateRecord(PrizeTemplateRecord prizeTemplate) {
        return rafflePort.savePrizeTemplateRecord(prizeTemplate);
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
    
    /**
     * Delete a prize template
     */
    public void deletePrizeTemplate(Long templateId) {
        rafflePort.deletePrizeTemplate(templateId);
    }
}