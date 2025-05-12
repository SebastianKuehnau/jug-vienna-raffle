package com.vaadin.demo.application.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vaadin.demo.application.adapter.out.meetupclient.MeetupAPIClientAdapter;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.adapter.out.persistence.data.Participant;
import com.vaadin.demo.application.adapter.out.persistence.repository.MeetupEventRepository;
import com.vaadin.demo.application.adapter.out.persistence.repository.MemberRepository;
import com.vaadin.demo.application.adapter.out.persistence.repository.ParticipantRepository;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeetupServiceAdapterTest {

    @Mock
    private MeetupAPIClientAdapter meetupApiClient;

    @Mock
    private MeetupEventRepository meetupEventRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ParticipantRepository participantRepository;

    private MeetupServiceAdapter meetupServiceAdapter;

    private MeetupEvent testEvent;
    private Member testMember;
    private Participant testParticipant;

    @BeforeEach
    void setUp() {
        meetupServiceAdapter = new MeetupServiceAdapter(
                meetupApiClient,
                meetupEventRepository,
                memberRepository,
                participantRepository
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
    }

    @Test
    void getEventByMeetupIdShouldReturnMappedEvent() {
        // Given
        String meetupId = "event123";
        when(meetupEventRepository.findByMeetupId(meetupId)).thenReturn(Optional.of(testEvent));

        // When
        Optional<EventRecord> result = meetupServiceAdapter.getEventByMeetupId(meetupId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEvent.getId(), result.get().id());
        assertEquals(testEvent.getMeetupId(), result.get().meetupId());
        assertEquals(testEvent.getTitle(), result.get().title());
        verify(meetupEventRepository).findByMeetupId(meetupId);
    }

    @Test
    void getAllEventsShouldReturnAllEvents() {
        // Given
        when(meetupEventRepository.findAll()).thenReturn(List.of(testEvent));

        // When
        List<EventRecord> result = meetupServiceAdapter.getAllEvents();

        // Then
        assertEquals(1, result.size());
        assertEquals(testEvent.getId(), result.get(0).id());
        verify(meetupEventRepository).findAll();
    }

    @Test
    void getParticipantsForEventShouldReturnParticipants() {
        // Given
        EventRecord eventRecord = Mapper.toEventRecord(testEvent);
        when(meetupEventRepository.findById(eventRecord.id())).thenReturn(Optional.of(testEvent));
        when(participantRepository.findByMeetupEvent(testEvent)).thenReturn(List.of(testParticipant));

        // When
        List<ParticipantRecord> result = meetupServiceAdapter.getParticipantsForEvent(eventRecord);

        // Then
        assertEquals(1, result.size());
        assertEquals(testParticipant.getId(), result.get(0).id());
        verify(meetupEventRepository).findById(eventRecord.id());
        verify(participantRepository).findByMeetupEvent(testEvent);
    }

    @Test
    void getRaffleEligibleParticipantsShouldFilterEligibleParticipants() {
        // Given
        EventRecord eventRecord = Mapper.toEventRecord(testEvent);
        when(meetupEventRepository.findById(eventRecord.id())).thenReturn(Optional.of(testEvent));
        when(participantRepository.findByMeetupEventAndRsvpStatus(testEvent, Participant.RSVPStatus.YES))
                .thenReturn(List.of(testParticipant));

        // When
        List<ParticipantRecord> result = meetupServiceAdapter.getRaffleEligibleParticipants(eventRecord);

        // Then
        assertEquals(1, result.size());
        assertEquals(testParticipant.getId(), result.get(0).id());
        verify(meetupEventRepository).findById(eventRecord.id());
        verify(participantRepository).findByMeetupEventAndRsvpStatus(testEvent, Participant.RSVPStatus.YES);
    }

    @Test
    void markParticipantEnteredRaffleShouldUpdateParticipant() {
        // Given
        Long participantId = 1L;
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(testParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(testParticipant);

        // When
        ParticipantRecord result = meetupServiceAdapter.markParticipantEnteredRaffle(participantId);

        // Then
        verify(participantRepository).findById(participantId);
        verify(participantRepository).save(any(Participant.class));
        assertTrue(testParticipant.getHasEnteredRaffle());
    }

    @Test
    void markParticipantAttendedAndEnteredRaffleShouldUpdateParticipant() {
        // Given
        Long participantId = 1L;
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(testParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(testParticipant);

        // When
        ParticipantRecord result = meetupServiceAdapter.markParticipantAttendedAndEnteredRaffle(participantId);

        // Then
        verify(participantRepository).findById(participantId);
        verify(participantRepository).save(any(Participant.class));
        assertTrue(testParticipant.getHasEnteredRaffle());
        assertEquals(Participant.AttendanceStatus.ATTENDED, testParticipant.getAttendanceStatus());
    }

    @Test
    void markParticipantNoShowAndEnteredRaffleShouldUpdateParticipant() {
        // Given
        Long participantId = 1L;
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(testParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(testParticipant);

        // When
        ParticipantRecord result = meetupServiceAdapter.markParticipantNoShowAndEnteredRaffle(participantId);

        // Then
        verify(participantRepository).findById(participantId);
        verify(participantRepository).save(any(Participant.class));
        assertTrue(testParticipant.getHasEnteredRaffle());
        assertEquals(Participant.AttendanceStatus.NO_SHOW, testParticipant.getAttendanceStatus());
    }

    @Test
    void resetRaffleEntryForEventShouldResetAllParticipants() {
        // Given
        EventRecord eventRecord = Mapper.toEventRecord(testEvent);
        when(meetupEventRepository.findById(eventRecord.id())).thenReturn(Optional.of(testEvent));
        when(participantRepository.findByMeetupEvent(testEvent)).thenReturn(List.of(testParticipant));

        // When
        meetupServiceAdapter.resetRaffleEntryForEvent(eventRecord);

        // Then
        verify(meetupEventRepository).findById(eventRecord.id());
        verify(participantRepository).findByMeetupEvent(testEvent);
        verify(participantRepository).saveAll(anyList());
        assertFalse(testParticipant.getHasEnteredRaffle());
    }

    // Additional integration tests would require an embedded database setup
}