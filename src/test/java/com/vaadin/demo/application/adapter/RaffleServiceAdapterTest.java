package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.adapter.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.persistence.data.Member;
import com.vaadin.demo.application.adapter.persistence.data.Participant;
import com.vaadin.demo.application.adapter.persistence.data.Prize;
import com.vaadin.demo.application.adapter.persistence.data.Raffle;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.adapter.persistence.repository.MeetupEventRepository;
import com.vaadin.demo.application.adapter.persistence.repository.PrizeRepository;
import com.vaadin.demo.application.adapter.persistence.repository.PrizeTemplateRepository;
import com.vaadin.demo.application.adapter.persistence.repository.RaffleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaffleServiceAdapterTest {

    @Mock
    private RaffleRepository raffleRepository;

    @Mock
    private PrizeRepository prizeRepository;

    @Mock
    private PrizeTemplateRepository prizeTemplateRepository;

    @Mock
    private MeetupEventRepository meetupEventRepository;

    @Mock
    private MeetupPort meetupPort;

    private RaffleServiceAdapter raffleServiceAdapter;

    private MeetupEvent testEvent;
    private Raffle testRaffle;
    private Prize testPrize;
    private Member testMember;
    private Participant testParticipant;
    private EventRecord testEventRecord;
    private PrizeRecord testPrizeRecord;
    private ParticipantRecord testParticipantRecord;

    @BeforeEach
    void setUp() {
        raffleServiceAdapter = new RaffleServiceAdapter(
                raffleRepository,
                prizeRepository,
                prizeTemplateRepository,
                meetupEventRepository,
                meetupPort
        );

        // Create test data
        testEvent = new MeetupEvent();
        testEvent.setId(1L);
        testEvent.setMeetupId("event123");
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(OffsetDateTime.now());
        testEvent.setEventUrl("http://test.url");
        testEvent.setStatus("active");

        testRaffle = new Raffle();
        testRaffle.setId(1L);
        testRaffle.setEvent(testEvent);
        testRaffle.setMeetup_event_id(testEvent.getMeetupId());

        testMember = new Member();
        testMember.setId(1L);
        testMember.setMeetupId("member123");
        testMember.setName("John Doe");
        testMember.setEmail("john@example.com");

        testParticipant = new Participant();
        testParticipant.setId(1L);
        testParticipant.setMeetupEvent(testEvent);
        testParticipant.setMember(testMember);
        testParticipant.setRsvpId("rsvp123");
        testParticipant.setRsvpStatus(Participant.RSVPStatus.YES);
        testParticipant.setAttendanceStatus(Participant.AttendanceStatus.UNKNOWN);
        testParticipant.setIsOrganizer(false);
        testParticipant.setHasEnteredRaffle(false);

        testPrize = new Prize();
        testPrize.setId(1L);
        testPrize.setName("Test Prize");
        testPrize.setRaffle(testRaffle);
        testPrize.setWinner(null);

        testRaffle.setPrizes(Set.of(testPrize));

        testEventRecord = Mapper.toEventRecord(testEvent);
        testPrizeRecord = Mapper.toPrizeRecord(testPrize);
        testParticipantRecord = Mapper.toParticipantRecord(testParticipant);
    }

    @Test
    void getRaffleByIdShouldReturnMappedRaffle() {
        // Given
        Long raffleId = 1L;
        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(testRaffle));

        // When
        Optional<RaffleRecord> result = raffleServiceAdapter.getRaffleById(raffleId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testRaffle.getId(), result.get().id());
        assertEquals(testEvent.getId(), result.get().event().id());
        verify(raffleRepository).findById(raffleId);
    }

    @Test
    void getPrizeByIdShouldReturnMappedPrize() {
        // Given
        Long prizeId = 1L;
        when(prizeRepository.findById(prizeId)).thenReturn(Optional.of(testPrize));

        // When
        Optional<PrizeRecord> result = raffleServiceAdapter.getPrizeById(prizeId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPrize.getId(), result.get().id());
        assertEquals(testPrize.getName(), result.get().name());
        verify(prizeRepository).findById(prizeId);
    }

    @Test
    void getRaffleByMeetupEventIdShouldReturnRaffle() {
        // Given
        String meetupEventId = "event123";
        when(raffleRepository.findByMeetupEventId(meetupEventId)).thenReturn(Optional.of(testRaffle));

        // When
        Optional<RaffleRecord> result = raffleServiceAdapter.getRaffleByMeetupEventId(meetupEventId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testRaffle.getId(), result.get().id());
        verify(raffleRepository).findByMeetupEventId(meetupEventId);
    }

    @Test
    void getPrizesForRaffleShouldReturnPrizes() {
        // Given
        RaffleRecord raffleRecord = Mapper.toRaffleRecord(testRaffle);
        when(raffleRepository.findById(raffleRecord.id())).thenReturn(Optional.of(testRaffle));
        when(prizeRepository.findByRaffle(testRaffle)).thenReturn(List.of(testPrize));

        // When
        List<PrizeRecord> result = raffleServiceAdapter.getPrizesForRaffle(raffleRecord);

        // Then
        assertEquals(1, result.size());
        assertEquals(testPrize.getId(), result.get(0).id());
        verify(raffleRepository).findById(raffleRecord.id());
        verify(prizeRepository).findByRaffle(testRaffle);
    }

    @Test
    void getEligibleParticipantsShouldDelegateToMeetupPort() {
        // Given
        RaffleRecord raffleRecord = Mapper.toRaffleRecord(testRaffle);
        when(meetupPort.getRaffleEligibleParticipants(raffleRecord.event()))
                .thenReturn(List.of(testParticipantRecord));

        // When
        List<ParticipantRecord> result = raffleServiceAdapter.getEligibleParticipants(raffleRecord);

        // Then
        assertEquals(1, result.size());
        assertEquals(testParticipantRecord.id(), result.get(0).id());
        verify(meetupPort).getRaffleEligibleParticipants(raffleRecord.event());
    }

    @Test
    void createRaffleShouldSaveNewRaffle() {
        // Given
        when(meetupEventRepository.findByMeetupId(testEventRecord.meetupId()))
                .thenReturn(Optional.of(testEvent));
        when(raffleRepository.save(any(Raffle.class))).thenReturn(testRaffle);

        // When
        RaffleRecord result = raffleServiceAdapter.createRaffle(testEventRecord);

        // Then
        assertNotNull(result);
        assertEquals(testRaffle.getId(), result.id());
        verify(meetupEventRepository).findByMeetupId(testEventRecord.meetupId());
        verify(raffleRepository).save(any(Raffle.class));
    }

    @Test
    void savePrizeShouldSavePrize() {
        // Given
        when(prizeRepository.findById(testPrizeRecord.id())).thenReturn(Optional.of(testPrize));
        when(raffleRepository.findById(testPrizeRecord.raffle().id())).thenReturn(Optional.of(testRaffle));
        when(prizeRepository.save(any(Prize.class))).thenReturn(testPrize);

        // When
        PrizeRecord result = raffleServiceAdapter.savePrize(testPrizeRecord);

        // Then
        assertNotNull(result);
        assertEquals(testPrize.getId(), result.id());
        verify(prizeRepository).findById(testPrizeRecord.id());
        verify(prizeRepository).save(any(Prize.class));
    }

    @Test
    void awardPrizeShouldUpdatePrizeWithWinner() {
        // Given
        when(prizeRepository.findById(testPrizeRecord.id())).thenReturn(Optional.of(testPrize));
        when(meetupPort.markParticipantAttendedAndEnteredRaffle(testParticipantRecord.id()))
                .thenReturn(testParticipantRecord);

        Prize updatedPrize = new Prize();
        updatedPrize.setId(testPrize.getId());
        updatedPrize.setName(testPrize.getName());
        updatedPrize.setRaffle(testPrize.getRaffle());
        updatedPrize.setWinner(testParticipant);

        when(prizeRepository.save(any(Prize.class))).thenReturn(updatedPrize);

        // When
        PrizeRecord result = raffleServiceAdapter.awardPrize(testPrizeRecord, testParticipantRecord);

        // Then
        assertNotNull(result);
        assertNotNull(result.winner());
        assertEquals(testParticipantRecord.id(), result.winner().id());
        verify(prizeRepository).findById(testPrizeRecord.id());
        verify(meetupPort).markParticipantAttendedAndEnteredRaffle(testParticipantRecord.id());
        verify(prizeRepository).save(any(Prize.class));
    }

    @Test
    void deletePrizeShouldDeletePrize() {
        // Given
        Long prizeId = 1L;
        doNothing().when(prizeRepository).deleteById(prizeId);

        // When
        raffleServiceAdapter.deletePrize(prizeId);

        // Then
        verify(prizeRepository).deleteById(prizeId);
    }

    // Additional integration tests would require an embedded database setup
}