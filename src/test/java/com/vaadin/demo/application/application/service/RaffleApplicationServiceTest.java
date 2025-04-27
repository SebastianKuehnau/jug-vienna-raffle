package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaffleApplicationServiceTest {

    @Mock
    private RafflePort rafflePort;
    
    @Mock
    private MeetupPort meetupPort;

    private RaffleApplicationService raffleApplicationService;

    private EventRecord sampleEvent;
    private RaffleRecord sampleRaffle;
    private PrizeRecord samplePrize;
    private ParticipantRecord sampleParticipant;

    @BeforeEach
    void setUp() {
        raffleApplicationService = new RaffleApplicationService(rafflePort, meetupPort);
        
        // Create sample test data
        sampleEvent = new EventRecord(
                1L,
                "event123",
                "Test Event",
                "Description",
                OffsetDateTime.now(),
                "Venue",
                "Link"
        );
        
        sampleRaffle = new RaffleRecord(
                1L,
                sampleEvent,
                sampleEvent.meetupId(),
                List.of()
        );
        
        samplePrize = new PrizeRecord(
                1L,
                "Test Prize",
                "Prize Description", 
                "Prize Template Text",
                false,
                null,
                sampleRaffle,
                "TEST123",
                LocalDate.now().plusMonths(3)
        );
        
        MemberRecord sampleMember = new MemberRecord(
                1L,
                "member123",
                "John Doe",
                "john@example.com",
                OffsetDateTime.now()
        );
        
        sampleParticipant = new ParticipantRecord(
                1L,
                sampleMember,
                sampleEvent,
                "rsvp123",
                false,
                false,
                ParticipantRecord.RsvpStatus.YES,
                ParticipantRecord.AttendanceStatus.UNKNOWN
        );
    }

    @Test
    void getRaffleByIdShouldDelegateToPort() {
        // Given
        Long raffleId = 1L;
        when(rafflePort.getRaffleById(raffleId)).thenReturn(Optional.of(sampleRaffle));

        // When
        Optional<RaffleRecord> result = raffleApplicationService.getRaffleById(raffleId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(sampleRaffle, result.get());
        verify(rafflePort).getRaffleById(raffleId);
    }

    @Test
    void getPrizeByIdShouldDelegateToPort() {
        // Given
        Long prizeId = 1L;
        when(rafflePort.getPrizeById(prizeId)).thenReturn(Optional.of(samplePrize));

        // When
        Optional<PrizeRecord> result = raffleApplicationService.getPrizeById(prizeId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(samplePrize, result.get());
        verify(rafflePort).getPrizeById(prizeId);
    }

    @Test
    void getRaffleByMeetupEventIdShouldDelegateToPort() {
        // Given
        String meetupEventId = "event123";
        when(rafflePort.getRaffleByMeetupEventId(meetupEventId)).thenReturn(Optional.of(sampleRaffle));

        // When
        Optional<RaffleRecord> result = raffleApplicationService.getRaffleByMeetupEventId(meetupEventId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(sampleRaffle, result.get());
        verify(rafflePort).getRaffleByMeetupEventId(meetupEventId);
    }

    @Test
    void getPrizesForRaffleShouldDelegateToPort() {
        // Given
        List<PrizeRecord> prizes = List.of(samplePrize);
        when(rafflePort.getPrizesForRaffle(sampleRaffle)).thenReturn(prizes);

        // When
        List<PrizeRecord> result = raffleApplicationService.getPrizesForRaffle(sampleRaffle);

        // Then
        assertEquals(prizes, result);
        verify(rafflePort).getPrizesForRaffle(sampleRaffle);
    }

    @Test
    void getEligibleParticipantsShouldDelegateToPort() {
        // Given
        List<ParticipantRecord> participants = List.of(sampleParticipant);
        when(rafflePort.getEligibleParticipants(sampleRaffle)).thenReturn(participants);

        // When
        List<ParticipantRecord> result = raffleApplicationService.getEligibleParticipants(sampleRaffle);

        // Then
        assertEquals(participants, result);
        verify(rafflePort).getEligibleParticipants(sampleRaffle);
    }

    @Test
    void createRaffleShouldDelegateToPort() {
        // Given
        when(rafflePort.createRaffle(sampleEvent)).thenReturn(sampleRaffle);

        // When
        RaffleRecord result = raffleApplicationService.createRaffle(sampleEvent);

        // Then
        assertEquals(sampleRaffle, result);
        verify(rafflePort).createRaffle(sampleEvent);
    }
    
    @Test
    void getAllRafflesShouldDelegateToPort() {
        // Given
        List<RaffleRecord> raffles = List.of(sampleRaffle);
        when(rafflePort.getAllRaffles()).thenReturn(raffles);

        // When
        List<RaffleRecord> result = raffleApplicationService.getAllRaffles();

        // Then
        assertEquals(raffles, result);
        verify(rafflePort).getAllRaffles();
    }
    
    @Test
    void createRaffleFromFormShouldDelegateToPort() {
        // Given
        String meetupEventId = "event123";
        when(meetupPort.getEventByMeetupId(meetupEventId)).thenReturn(Optional.of(sampleEvent));
        when(rafflePort.createRaffle(sampleEvent)).thenReturn(sampleRaffle);

        // When
        RaffleFormRecord result = raffleApplicationService.createRaffleFromForm(meetupEventId);

        // Then
        assertNotNull(result);
        assertEquals(sampleRaffle.id(), result.id());
        assertEquals(meetupEventId, result.meetupEventId());
        verify(meetupPort).getEventByMeetupId(meetupEventId);
        verify(rafflePort).createRaffle(sampleEvent);
    }

    @Test
    void saveRaffleShouldDelegateToPort() {
        // Given
        when(rafflePort.saveRaffle(sampleRaffle)).thenReturn(sampleRaffle);

        // When
        RaffleRecord result = raffleApplicationService.saveRaffle(sampleRaffle);

        // Then
        assertEquals(sampleRaffle, result);
        verify(rafflePort).saveRaffle(sampleRaffle);
    }

    @Test
    void savePrizeShouldDelegateToPort() {
        // Given
        when(rafflePort.savePrize(samplePrize)).thenReturn(samplePrize);

        // When
        PrizeRecord result = raffleApplicationService.savePrize(samplePrize);

        // Then
        assertEquals(samplePrize, result);
        verify(rafflePort).savePrize(samplePrize);
    }

    @Test
    void awardPrizeShouldDelegateToPort() {
        // Given
        PrizeRecord prizeWithWinner = new PrizeRecord(
                samplePrize.id(),
                samplePrize.name(),
                samplePrize.description(),
                samplePrize.templateText(),
                samplePrize.isTemplate(),
                sampleParticipant,
                samplePrize.raffle(),
                samplePrize.voucherCode(),
                samplePrize.validUntil()
        );
        when(rafflePort.awardPrize(samplePrize, sampleParticipant)).thenReturn(prizeWithWinner);

        // When
        PrizeRecord result = raffleApplicationService.awardPrize(samplePrize, sampleParticipant);

        // Then
        assertEquals(prizeWithWinner, result);
        assertEquals(sampleParticipant, result.winner());
        verify(rafflePort).awardPrize(samplePrize, sampleParticipant);
    }

    @Test
    void deletePrizeShouldDelegateToPort() {
        // Given
        Long prizeId = 1L;
        doNothing().when(rafflePort).deletePrize(prizeId);

        // When
        raffleApplicationService.deletePrize(prizeId);

        // Then
        verify(rafflePort).deletePrize(prizeId);
    }
}