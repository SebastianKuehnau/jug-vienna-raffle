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
public class RaffleApplicationServiceImpl implements RaffleApplicationService {

    private final RafflePort rafflePort;
    private final MeetupPort meetupPort;

    public RaffleApplicationServiceImpl(RafflePort rafflePort, MeetupPort meetupPort) {
        this.rafflePort = rafflePort;
        this.meetupPort = meetupPort;
    }

    /**
     * Get a raffle by ID
     */
    @Override
    public Optional<RaffleRecord> getRaffleById(Long id) {
        return rafflePort.getRaffleById(id);
    }

    /**
     * Get all raffles
     */
    @Override
    public List<RaffleRecord> getAllRaffles() {
        return rafflePort.getAllRaffles();
    }

    /**
     * Get a prize by ID
     */
    @Override
    public Optional<PrizeRecord> getPrizeById(Long id) {
        return rafflePort.getPrizeById(id);
    }

    /**
     * Get a raffle by Meetup event ID
     */
    @Override
    public Optional<RaffleRecord> getRaffleByMeetupEventId(String meetupEventId) {
        return rafflePort.getRaffleByMeetupEventId(meetupEventId);
    }

    /**
     * Get prizes for a raffle
     */
    @Override
    public List<PrizeRecord> getPrizesForRaffle(RaffleRecord raffle) {
        return rafflePort.getPrizesForRaffle(raffle);
    }

    /**
     * Get all prize templates as PrizeTemplateRecord objects
     */
    @Override
    public List<PrizeTemplateRecord> getAllPrizeTemplateRecords() {
        return rafflePort.getAllPrizeTemplateRecords();
    }


    /**
     * Get prize templates by name as PrizeTemplateRecord objects (partial match)
     */
    @Override
    public List<PrizeTemplateRecord> getPrizeTemplateRecordsByName(String namePattern) {
        return rafflePort.getPrizeTemplateRecordsByName(namePattern);
    }

    /**
     * Get a prize template by ID as PrizeTemplateRecord
     */
    @Override
    public Optional<PrizeTemplateRecord> getPrizeTemplateRecordById(Long id) {
        return rafflePort.getPrizeTemplateRecordById(id);
    }

    /**
     * Create a new prize from a template record
     */
    @Override
    public PrizeRecord createPrizeFromTemplateRecord(Long templateId, RaffleRecord raffle,
        String voucherCode) {
        return rafflePort.createPrizeFromTemplateRecord(templateId, raffle, voucherCode);
    }

    /**
     * Get eligible participants for a raffle
     */
    @Override
    public List<ParticipantRecord> getEligibleParticipants(RaffleRecord raffle) {
        return rafflePort.getEligibleParticipants(raffle);
    }

    /**
     * Create a new raffle for an event
     */
    @Override
    public RaffleRecord createRaffle(EventRecord event) {
        return rafflePort.createRaffle(event);
    }

    /**
     * Save a raffle
     */
    @Override
    public RaffleRecord saveRaffle(RaffleRecord raffle) {
        return rafflePort.saveRaffle(raffle);
    }

    /**
     * Save a prize
     */
    @Override
    public PrizeRecord savePrize(PrizeRecord prize) {
        return rafflePort.savePrize(prize);
    }

    /**
     * Save a prize form
     * This converts the form data to a domain record and saves it
     */
    @Override
    public PrizeFormRecord savePrizeForm(PrizeFormRecord prizeForm, RaffleRecord raffle) {
        PrizeRecord prizeRecord = prizeForm.toPrizeRecord(raffle);
        PrizeRecord savedPrize = rafflePort.savePrize(prizeRecord);
        return PrizeFormRecord.fromPrizeRecord(savedPrize);
    }

    /**
     * Save a dialog form
     * This converts the form data to a domain record and saves it
     */
    @Override
    public PrizeDialogFormRecord savePrizeDialogForm(PrizeDialogFormRecord dialogForm,
        RaffleRecord raffle) {

        // Save as prize
        PrizeRecord prizeRecord = dialogForm.toPrizeRecord(raffle);
        PrizeRecord savedPrize = rafflePort.savePrize(prizeRecord);
        return PrizeDialogFormRecord.fromPrizeRecord(savedPrize);
    }

    /**
     * Get all raffles as form records
     */
    @Override
    public List<RaffleFormRecord> getAllRaffleForms() {
        return getAllRaffles().stream()
                .map(RaffleFormRecord::fromRaffleRecord)
                .collect(Collectors.toList());
    }

    /**
     * Create a raffle from a form
     */
    @Override
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
    @Override
    public Optional<PrizeFormRecord> getPrizeFormById(Long id) {
        return rafflePort.getPrizeById(id)
                .map(PrizeFormRecord::fromPrizeRecord);
    }

    /**
     * Get a prize dialog form by ID
     */
    @Override
    public Optional<PrizeDialogFormRecord> getPrizeDialogFormById(Long id) {
        return rafflePort.getPrizeById(id)
                .map(PrizeDialogFormRecord::fromPrizeRecord);
    }

    /**
     * Get a prize template dialog form by ID
     */
    @Override
    public Optional<PrizeDialogFormRecord> getPrizeTemplateDialogFormById(Long id) {
        return rafflePort.getPrizeTemplateRecordById(id)
                .map(PrizeDialogFormRecord::fromPrizeTemplateRecord);
    }

    /**
     * Create an empty prize dialog form
     */
    @Override
    public PrizeDialogFormRecord createEmptyPrizeDialogForm(boolean isTemplate) {
        return isTemplate ?
                PrizeDialogFormRecord.emptyTemplate() :
                PrizeDialogFormRecord.emptyPrize();
    }

    /**
     * Delete a prize dialog form
     */
    @Override
    public void deletePrizeDialogForm(PrizeDialogFormRecord form) {
            // Delete prize
            if (form.id() != null) {
                rafflePort.deletePrize(form.id());
            }
    }

    /**
     * Save a prize template record
     */
    @Override
    public PrizeTemplateRecord savePrizeTemplateRecord(PrizeTemplateRecord prizeTemplate) {
        return rafflePort.savePrizeTemplateRecord(prizeTemplate);
    }

    /**
     * Award a prize to a participant
     */
    @Override
    public PrizeRecord awardPrize(PrizeRecord prize, ParticipantRecord participant) {
        return rafflePort.awardPrize(prize, participant);
    }

    /**
     * Delete a prize
     */
    @Override
    public void deletePrize(Long prizeId) {
        rafflePort.deletePrize(prizeId);
    }

    /**
     * Delete a prize template
     */
    @Override
    public void deletePrizeTemplate(Long templateId) {
        rafflePort.deletePrizeTemplate(templateId);
    }
}