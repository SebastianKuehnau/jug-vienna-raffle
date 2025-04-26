package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.repository.MeetupEventRepository;
import com.vaadin.demo.application.repository.PrizeRepository;
import com.vaadin.demo.application.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RafflePort interface
 * This service is responsible for all Raffle-related operations
 */
// Keeping this for backward compatibility
// The new service implementation is in the adapter package
@Service(value = "legacyRaffleService")
@RequiredArgsConstructor
@Slf4j
public class RaffleService {

    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;
    private final com.vaadin.demo.application.adapter.MeetupServiceAdapter meetupService;
    private final MeetupEventRepository meetupEventRepository;

    @Transactional(readOnly = true)
    public Optional<Raffle> getRaffleById(Long id) {
        return raffleRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Prize> getPrizeById(Long id) {
        return prizeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Raffle> getRaffleByMeetupEventId(String meetupEventId) {
        // Try both methods for compatibility
        Optional<Raffle> result = raffleRepository.findByMeetupEventId(meetupEventId);
        if (result.isEmpty()) {
            result = raffleRepository.findByEvent_MeetupId(meetupEventId);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Prize> getPrizesForRaffle(Raffle raffle) {
        return prizeRepository.findByRaffle(raffle);
    }

    @Transactional(readOnly = true)
    public List<Participant> getEligibleParticipants(Raffle raffle) {
        MeetupEvent event = raffle.getEvent();
        if (event != null) {
            return meetupService.getRaffleEligibleParticipants(event);
        }
        return List.of();
    }

    @Transactional
    public Raffle createRaffle(EventRecord eventRecord) {
        // First convert the domain eventRecord to a JPA entity
        MeetupEvent meetupEvent = meetupEventRepository.findByMeetupId(eventRecord.meetupId())
                .orElseThrow(() -> new IllegalArgumentException("MeetupEvent not found with ID: " + eventRecord.meetupId()));
        
        // Check if a raffle already exists for this event
        if (getRaffleByMeetupEventId(meetupEvent.getMeetupId()).isPresent()) {
            throw new IllegalStateException("A raffle already exists for this event");
        }
        
        Raffle raffle = new Raffle();
        raffle.setMeetup_event_id(meetupEvent.getMeetupId());
        raffle.setEvent(meetupEvent);
        return saveRaffle(raffle);
    }

    @Transactional
    public Raffle saveRaffle(Raffle raffle) {
        return raffleRepository.save(raffle);
    }

    @Transactional
    public Prize savePrize(Prize prize) {
        return prizeRepository.save(prize);
    }

    @Transactional
    public Prize awardPrize(Prize prize, Participant participant) {
        prize.setWinner(participant);
        // Mark participant as attended and having entered the raffle
        meetupService.markParticipantAttendedAndEnteredRaffle(participant.getId());
        return savePrize(prize);
    }

    @Transactional
    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }
}