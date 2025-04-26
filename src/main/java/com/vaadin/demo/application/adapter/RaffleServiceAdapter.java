package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.domain.model.*;
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
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Adapter implementation of the RafflePort interface
 * This service is responsible for all Raffle-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RaffleServiceAdapter implements RafflePort {

    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;
    private final MeetupEventRepository meetupEventRepository;
    private final MeetupPort meetupPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<RaffleRecord> getRaffleById(Long id) {
        return raffleRepository.findById(id).map(Mapper::toRaffleRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PrizeRecord> getPrizeById(Long id) {
        return prizeRepository.findById(id).map(Mapper::toPrizeRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RaffleRecord> getRaffleByMeetupEventId(String meetupEventId) {
        // Try both methods for compatibility
        Optional<Raffle> result = raffleRepository.findByMeetupEventId(meetupEventId);
        if (result.isEmpty()) {
            result = raffleRepository.findByEvent_MeetupId(meetupEventId);
        }
        return result.map(Mapper::toRaffleRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrizeRecord> getPrizesForRaffle(RaffleRecord raffle) {
        Raffle raffleEntity = raffleRepository.findById(raffle.id())
            .orElseThrow(() -> new IllegalArgumentException("Raffle not found: " + raffle.id()));
            
        return prizeRepository.findByRaffle(raffleEntity).stream()
            .map(Mapper::toPrizeRecord)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantRecord> getEligibleParticipants(RaffleRecord raffle) {
        if (raffle.event() == null) {
            return List.of();
        }
        
        return meetupPort.getRaffleEligibleParticipants(raffle.event());
    }

    @Override
    @Transactional
    public RaffleRecord createRaffle(EventRecord eventRecord) {
        // Check if a raffle already exists for this event
        if (getRaffleByMeetupEventId(eventRecord.meetupId()).isPresent()) {
            throw new IllegalStateException("A raffle already exists for this event");
        }
        
        // Get the actual MeetupEvent entity from the database
        MeetupEvent eventEntity = meetupEventRepository.findByMeetupId(eventRecord.meetupId())
            .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventRecord.meetupId()));
        
        Raffle raffle = new Raffle();
        raffle.setMeetup_event_id(eventRecord.meetupId());
        raffle.setEvent(eventEntity);
        
        Raffle savedRaffle = raffleRepository.save(raffle);
        return Mapper.toRaffleRecord(savedRaffle);
    }

    @Override
    @Transactional
    public RaffleRecord saveRaffle(RaffleRecord raffleRecord) {
        // Get the existing entity from the database
        Raffle existingRaffle = raffleRepository.findById(raffleRecord.id())
            .orElseThrow(() -> new IllegalArgumentException("Raffle not found: " + raffleRecord.id()));
        
        // Update only the fields that can be changed through the record
        // In this case, there's nothing to update specifically
        
        Raffle savedRaffle = raffleRepository.save(existingRaffle);
        return Mapper.toRaffleRecord(savedRaffle);
    }

    @Override
    @Transactional
    public PrizeRecord savePrize(PrizeRecord prizeRecord) {
        // Find existing prize or create a new one
        Prize prize = prizeRecord.id() != null 
            ? prizeRepository.findById(prizeRecord.id())
                .orElseThrow(() -> new IllegalArgumentException("Prize not found: " + prizeRecord.id()))
            : new Prize();
        
        // Update fields
        prize.setName(prizeRecord.name());
        
        // Set raffle if available
        if (prizeRecord.raffle() != null && prizeRecord.raffle().id() != null) {
            Raffle raffle = raffleRepository.findById(prizeRecord.raffle().id())
                .orElseThrow(() -> new IllegalArgumentException("Raffle not found: " + prizeRecord.raffle().id()));
            prize.setRaffle(raffle);
        }
        
        // Winner is handled separately in awardPrize method
        
        Prize savedPrize = prizeRepository.save(prize);
        return Mapper.toPrizeRecord(savedPrize);
    }

    @Override
    @Transactional
    public PrizeRecord awardPrize(PrizeRecord prizeRecord, ParticipantRecord participantRecord) {
        // Find the actual entities
        Prize prize = prizeRepository.findById(prizeRecord.id())
            .orElseThrow(() -> new IllegalArgumentException("Prize not found: " + prizeRecord.id()));
        
        // Get participant by ID
        ParticipantRecord updatedParticipant = meetupPort.markParticipantAttendedAndEnteredRaffle(participantRecord.id());
        
        // The MeetupPort has already updated the participant in the database
        // Now we just need to get a reference to it
        Participant participant = new Participant();
        participant.setId(updatedParticipant.id());
        
        // Set the winner
        prize.setWinner(participant);
        prize.setWinnerName(updatedParticipant.member() != null ? updatedParticipant.member().name() : null);
        
        Prize savedPrize = prizeRepository.save(prize);
        return Mapper.toPrizeRecord(savedPrize);
    }

    @Override
    @Transactional
    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }
}