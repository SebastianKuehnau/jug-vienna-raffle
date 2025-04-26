package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.port.MeetupPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetupApplicationServiceTest {

    @Mock
    private MeetupPort meetupPort;

    private MeetupApplicationService meetupApplicationService;

    private EventRecord sampleEvent;
    private ParticipantRecord sampleParticipant;

    @BeforeEach
    void setUp() {
        meetupApplicationService = new MeetupApplicationService(meetupPort);
        
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
        
        MemberRecord sampleMember = new MemberRecord(
                1L,
                "member123",
                "John Doe",
                "john@example.com"
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
    void getEventByMeetupIdShouldDelegateToPort() {
        // Given
        String meetupId = "event123";
        when(meetupPort.getEventByMeetupId(meetupId)).thenReturn(Optional.of(sampleEvent));

        // When
        Optional<EventRecord> result = meetupApplicationService.getEventByMeetupId(meetupId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(sampleEvent, result.get());
        verify(meetupPort).getEventByMeetupId(meetupId);
    }

    @Test
    void getAllEventsShouldDelegateToPort() {
        // Given
        List<EventRecord> events = List.of(sampleEvent);
        when(meetupPort.getAllEvents()).thenReturn(events);

        // When
        List<EventRecord> result = meetupApplicationService.getAllEvents();

        // Then
        assertEquals(events, result);
        verify(meetupPort).getAllEvents();
    }

    @Test
    void getParticipantsForEventShouldDelegateToPort() {
        // Given
        List<ParticipantRecord> participants = List.of(sampleParticipant);
        when(meetupPort.getParticipantsForEvent(sampleEvent)).thenReturn(participants);

        // When
        List<ParticipantRecord> result = meetupApplicationService.getParticipantsForEvent(sampleEvent);

        // Then
        assertEquals(participants, result);
        verify(meetupPort).getParticipantsForEvent(sampleEvent);
    }

    @Test
    void getRaffleEligibleParticipantsShouldDelegateToPort() {
        // Given
        List<ParticipantRecord> participants = List.of(sampleParticipant);
        when(meetupPort.getRaffleEligibleParticipants(sampleEvent)).thenReturn(participants);

        // When
        List<ParticipantRecord> result = meetupApplicationService.getRaffleEligibleParticipants(sampleEvent);

        // Then
        assertEquals(participants, result);
        verify(meetupPort).getRaffleEligibleParticipants(sampleEvent);
    }

    @Test
    void importEventShouldDelegateToPort() {
        // Given
        String meetupId = "event123";
        when(meetupPort.importEvent(meetupId)).thenReturn(sampleEvent);

        // When
        EventRecord result = meetupApplicationService.importEvent(meetupId);

        // Then
        assertEquals(sampleEvent, result);
        verify(meetupPort).importEvent(meetupId);
    }

    @Test
    void syncEventMembersShouldDelegateToPort() {
        // Given
        Long eventId = 1L;
        int expectedCount = 5;
        when(meetupPort.syncEventMembers(eventId)).thenReturn(expectedCount);

        // When
        int result = meetupApplicationService.syncEventMembers(eventId);

        // Then
        assertEquals(expectedCount, result);
        verify(meetupPort).syncEventMembers(eventId);
    }

    @Test
    void syncEventMembersByMeetupIdShouldDelegateToPort() {
        // Given
        String meetupId = "event123";
        int expectedCount = 5;
        when(meetupPort.syncEventMembersByMeetupId(meetupId)).thenReturn(expectedCount);

        // When
        int result = meetupApplicationService.syncEventMembersByMeetupId(meetupId);

        // Then
        assertEquals(expectedCount, result);
        verify(meetupPort).syncEventMembersByMeetupId(meetupId);
    }

    @Test
    void markParticipantEnteredRaffleShouldDelegateToPort() {
        // Given
        Long participantId = 1L;
        when(meetupPort.markParticipantEnteredRaffle(participantId)).thenReturn(sampleParticipant);

        // When
        ParticipantRecord result = meetupApplicationService.markParticipantEnteredRaffle(participantId);

        // Then
        assertEquals(sampleParticipant, result);
        verify(meetupPort).markParticipantEnteredRaffle(participantId);
    }

    @Test
    void markParticipantAttendedAndEnteredRaffleShouldDelegateToPort() {
        // Given
        Long participantId = 1L;
        when(meetupPort.markParticipantAttendedAndEnteredRaffle(participantId)).thenReturn(sampleParticipant);

        // When
        ParticipantRecord result = meetupApplicationService.markParticipantAttendedAndEnteredRaffle(participantId);

        // Then
        assertEquals(sampleParticipant, result);
        verify(meetupPort).markParticipantAttendedAndEnteredRaffle(participantId);
    }

    @Test
    void markParticipantNoShowAndEnteredRaffleShouldDelegateToPort() {
        // Given
        Long participantId = 1L;
        when(meetupPort.markParticipantNoShowAndEnteredRaffle(participantId)).thenReturn(sampleParticipant);

        // When
        ParticipantRecord result = meetupApplicationService.markParticipantNoShowAndEnteredRaffle(participantId);

        // Then
        assertEquals(sampleParticipant, result);
        verify(meetupPort).markParticipantNoShowAndEnteredRaffle(participantId);
    }

    @Test
    void resetRaffleEntryForEventShouldDelegateToPort() {
        // Given
        doNothing().when(meetupPort).resetRaffleEntryForEvent(sampleEvent);

        // When
        meetupApplicationService.resetRaffleEntryForEvent(sampleEvent);

        // Then
        verify(meetupPort).resetRaffleEntryForEvent(sampleEvent);
    }
}