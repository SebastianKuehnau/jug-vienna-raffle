package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.domain.port.MeetupPort;
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
    private final MeetupPort meetupPort;
    
    public RaffleApplicationService(RafflePort rafflePort, MeetupPort meetupPort) {
        this.rafflePort = rafflePort;
        this.meetupPort = meetupPort;
    }
    
    /**
     * Get a raffle by ID
     */
    public Optional<RaffleRecord> getRaffleById(Long id) {
        return rafflePort.getRaffleById(id);
    }
    
    /**
     * Get all raffles
     */
    public List<RaffleRecord> getAllRaffles() {
        return rafflePort.getAllRaffles();
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
     * Save a dialog form 
     * This converts the form data to a domain record and saves it
     */
    public PrizeDialogFormRecord savePrizeDialogForm(PrizeDialogFormRecord dialogForm, RaffleRecord raffle) {
        if (dialogForm.isTemplate()) {
            // Save as template
            PrizeTemplateRecord templateRecord = dialogForm.toPrizeTemplateRecord();
            PrizeTemplateRecord savedTemplate = rafflePort.savePrizeTemplateRecord(templateRecord);
            return PrizeDialogFormRecord.fromPrizeTemplateRecord(savedTemplate);
        } else {
            // Save as prize
            PrizeRecord prizeRecord = dialogForm.toPrizeRecord(raffle);
            PrizeRecord savedPrize = rafflePort.savePrize(prizeRecord);
            return PrizeDialogFormRecord.fromPrizeRecord(savedPrize);
        }
    }
    
    /**
     * Get all raffles as form records
     */
    public List<RaffleFormRecord> getAllRaffleForms() {
        return getAllRaffles().stream()
                .map(RaffleFormRecord::fromRaffleRecord)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a raffle from a form
     */
    public RaffleFormRecord createRaffleFromForm(String meetupEventId) {
        Optional<EventRecord> event = meetupPort.getEventByMeetupId(meetupEventId);
        if (event.isEmpty()) {
            throw new IllegalArgumentException("Event not found with ID: " + meetupEventId);
        }
        
        RaffleRecord newRaffle = createRaffle(event.get());
        return RaffleFormRecord.fromRaffleRecord(newRaffle);
    }
    
    /**
     * Get a prize form by ID
     */
    public Optional<PrizeFormRecord> getPrizeFormById(Long id) {
        return rafflePort.getPrizeById(id)
                .map(PrizeFormRecord::fromPrizeRecord);
    }
    
    /**
     * Get a prize dialog form by ID
     */
    public Optional<PrizeDialogFormRecord> getPrizeDialogFormById(Long id) {
        return rafflePort.getPrizeById(id)
                .map(PrizeDialogFormRecord::fromPrizeRecord);
    }
    
    /**
     * Get a prize template dialog form by ID
     */
    public Optional<PrizeDialogFormRecord> getPrizeTemplateDialogFormById(Long id) {
        return rafflePort.getPrizeTemplateRecordById(id)
                .map(PrizeDialogFormRecord::fromPrizeTemplateRecord);
    }
    
    /**
     * Create an empty prize dialog form
     */
    public PrizeDialogFormRecord createEmptyPrizeDialogForm(boolean isTemplate) {
        return isTemplate ? 
                PrizeDialogFormRecord.emptyTemplate() : 
                PrizeDialogFormRecord.emptyPrize();
    }
    
    /**
     * Delete a prize dialog form
     */
    public void deletePrizeDialogForm(PrizeDialogFormRecord form) {
        if (form.isTemplate()) {
            // Delete template
            if (form.id() != null) {
                rafflePort.deletePrizeTemplate(form.id());
            }
        } else {
            // Delete prize
            if (form.id() != null) {
                rafflePort.deletePrize(form.id());
            }
        }
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