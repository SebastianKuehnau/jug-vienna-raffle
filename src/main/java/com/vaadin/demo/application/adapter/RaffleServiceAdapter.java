package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.data.*;
import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.repository.MeetupEventRepository;
import com.vaadin.demo.application.repository.PrizeRepository;
import com.vaadin.demo.application.repository.PrizeTemplateRepository;
import com.vaadin.demo.application.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final PrizeTemplateRepository prizeTemplateRepository;
    private final MeetupEventRepository meetupEventRepository;
    private final MeetupPort meetupPort;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    public List<RaffleRecord> getAllRaffles() {
        return raffleRepository.findAll().stream()
            .map(Mapper::toRaffleRecord)
            .collect(Collectors.toList());
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
    public List<PrizeRecord> getAllPrizeTemplates() {
        return prizeRepository.findByTemplateTrue().stream()
            .map(Mapper::toPrizeRecord)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrizeTemplateRecord> getAllPrizeTemplateRecords() {
        return prizeTemplateRepository.findAll().stream()
            .map(Mapper::toPrizeTemplateRecord)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrizeRecord> getPrizeTemplatesByName(String namePattern) {
        return prizeRepository.findByTemplateTrueAndNameContaining(namePattern).stream()
            .map(Mapper::toPrizeRecord)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrizeTemplateRecord> getPrizeTemplateRecordsByName(String namePattern) {
        return prizeTemplateRepository.findByNameContainingIgnoreCase(namePattern).stream()
            .map(Mapper::toPrizeTemplateRecord)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PrizeRecord> getPrizeTemplateById(Long id) {
        return prizeRepository.findById(id)
            .filter(Prize::isTemplate)
            .map(Mapper::toPrizeRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PrizeTemplateRecord> getPrizeTemplateRecordById(Long id) {
        return prizeTemplateRepository.findById(id)
            .map(Mapper::toPrizeTemplateRecord);
    }
    
    @Override
    @Transactional
    public PrizeRecord createPrizeFromTemplate(Long templateId, RaffleRecord raffle, String voucherCode) {
        // Get the template
        Prize template = prizeRepository.findById(templateId)
            .filter(Prize::isTemplate)
            .orElseThrow(() -> new IllegalArgumentException("Prize template not found: " + templateId));
        
        // Create a new prize from the template
        Prize prize = template.createFromTemplate();
        prize.setVoucherCode(voucherCode);
        
        // Set raffle
        Raffle raffleEntity = raffleRepository.findById(raffle.id())
            .orElseThrow(() -> new IllegalArgumentException("Raffle not found: " + raffle.id()));
        prize.setRaffle(raffleEntity);
        
        // Process template text with voucher code
        String raffleDate = raffleEntity.getEvent().getDateTime() != null ? 
            raffleEntity.getEvent().getDateTime().format(DATE_FORMATTER) : "";
        prize.setTemplateText(prize.processTemplateText(raffleDate, null, voucherCode));
        
        // Save and return
        Prize savedPrize = prizeRepository.save(prize);
        return Mapper.toPrizeRecord(savedPrize);
    }
    
    @Override
    @Transactional
    public PrizeRecord createPrizeFromTemplateRecord(Long templateId, RaffleRecord raffle, String voucherCode) {
        // Get the template
        PrizeTemplate template = prizeTemplateRepository.findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Prize template not found: " + templateId));
        
        // Create a new prize from the template
        Prize prize = template.createPrize();
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            prize.setVoucherCode(voucherCode);
        }
        
        // Set raffle
        Raffle raffleEntity = raffleRepository.findById(raffle.id())
            .orElseThrow(() -> new IllegalArgumentException("Raffle not found: " + raffle.id()));
        prize.setRaffle(raffleEntity);
        
        // Process template text with voucher code
        String raffleDate = raffleEntity.getEvent().getDateTime() != null ? 
            raffleEntity.getEvent().getDateTime().format(DATE_FORMATTER) : "";
        prize.setTemplateText(prize.processTemplateText(raffleDate, null, voucherCode));
        
        // Save and return
        Prize savedPrize = prizeRepository.save(prize);
        return Mapper.toPrizeRecord(savedPrize);
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
        prize.setDescription(prizeRecord.description());
        prize.setTemplateText(prizeRecord.templateText());
        prize.setTemplate(false); // Ensure it's not a template
        prize.setVoucherCode(prizeRecord.voucherCode());
        prize.setValidUntil(prizeRecord.validUntil());
        
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
    public PrizeRecord savePrizeTemplate(PrizeRecord prizeTemplate) {
        // Find existing template or create a new one
        Prize template = prizeTemplate.id() != null 
            ? prizeRepository.findById(prizeTemplate.id())
                .orElseThrow(() -> new IllegalArgumentException("Prize template not found: " + prizeTemplate.id()))
            : new Prize();
        
        // Update fields
        template.setName(prizeTemplate.name());
        template.setDescription(prizeTemplate.description());
        template.setTemplateText(prizeTemplate.templateText());
        template.setTemplate(true); // Ensure it's a template
        template.setRaffle(null); // Templates are not associated with raffles
        template.setVoucherCode(prizeTemplate.voucherCode());
        template.setValidUntil(prizeTemplate.validUntil());
        
        Prize savedTemplate = prizeRepository.save(template);
        return Mapper.toPrizeRecord(savedTemplate);
    }
    
    @Override
    @Transactional
    public PrizeTemplateRecord savePrizeTemplateRecord(PrizeTemplateRecord prizeTemplateRecord) {
        // Find existing template or create a new one
        PrizeTemplate template = prizeTemplateRecord.id() != null 
            ? prizeTemplateRepository.findById(prizeTemplateRecord.id())
                .orElseThrow(() -> new IllegalArgumentException("Prize template not found: " + prizeTemplateRecord.id()))
            : new PrizeTemplate();
        
        // Update fields
        template.setName(prizeTemplateRecord.name());
        template.setDescription(prizeTemplateRecord.description());
        template.setTemplateText(prizeTemplateRecord.templateText());
        template.setVoucherCode(prizeTemplateRecord.voucherCode());
        template.setValidUntil(prizeTemplateRecord.validUntil());
        
        PrizeTemplate savedTemplate = prizeTemplateRepository.save(template);
        return Mapper.toPrizeTemplateRecord(savedTemplate);
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
        
        // Update the template text with winner name if applicable
        if (prize.getTemplateText() != null) {
            Raffle raffle = prize.getRaffle();
            String raffleDate = raffle != null && raffle.getEvent() != null && raffle.getEvent().getDateTime() != null ?
                raffle.getEvent().getDateTime().format(DATE_FORMATTER) : "";
            
            String processedText = prize.processTemplateText(
                raffleDate, 
                updatedParticipant.member() != null ? updatedParticipant.member().name() : null, 
                prize.getVoucherCode()
            );
            prize.setTemplateText(processedText);
        }
        
        Prize savedPrize = prizeRepository.save(prize);
        return Mapper.toPrizeRecord(savedPrize);
    }

    @Override
    @Transactional
    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }
    
    @Override
    @Transactional
    public void deletePrizeTemplate(Long templateId) {
        // Check if it's a Prize in template mode or a PrizeTemplate
        if (prizeRepository.existsById(templateId)) {
            prizeRepository.deleteById(templateId);
        } else if (prizeTemplateRepository.existsById(templateId)) {
            prizeTemplateRepository.deleteById(templateId);
        } else {
            throw new IllegalArgumentException("Prize template not found: " + templateId);
        }
    }
}