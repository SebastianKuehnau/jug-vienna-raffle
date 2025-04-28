package com.vaadin.demo.application.domain.model;

import com.vaadin.demo.application.adapter.Mapper;
import com.vaadin.demo.application.adapter.persistence.data.Participant;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantRecordTest {

    @Test
    void testParticipantRecordCreation() {
        // Given
        Long id = 1L;
        MemberRecord member = new MemberRecord(1L, "member123", "John Doe", "john@example.com", OffsetDateTime.now());
        EventRecord event = EventRecord.simple(1L, "event123", "Test Event");
        String rsvpId = "rsvp123";
        boolean isOrganizer = false;
        boolean hasEnteredRaffle = false;
        ParticipantRecord.RsvpStatus rsvpStatus = ParticipantRecord.RsvpStatus.YES;
        ParticipantRecord.AttendanceStatus attendanceStatus = ParticipantRecord.AttendanceStatus.UNKNOWN;

        // When
        ParticipantRecord participant = new ParticipantRecord(
                id, member, event, rsvpId, isOrganizer, hasEnteredRaffle, rsvpStatus, attendanceStatus);

        // Then
        assertEquals(id, participant.id());
        assertEquals(member, participant.member());
        assertEquals(event, participant.event());
        assertEquals(rsvpId, participant.rsvpId());
        assertEquals(isOrganizer, participant.isOrganizer());
        assertEquals(hasEnteredRaffle, participant.hasEnteredRaffle());
        assertEquals(rsvpStatus, participant.rsvpStatus());
        assertEquals(attendanceStatus, participant.attendanceStatus());
    }

    @Test
    void testWithAttendanceStatus() {
        // Given
        ParticipantRecord participant = createSampleParticipant();
        ParticipantRecord.AttendanceStatus newStatus = ParticipantRecord.AttendanceStatus.ATTENDED;

        // When
        ParticipantRecord updatedParticipant = participant.withAttendanceStatus(newStatus);

        // Then
        assertEquals(newStatus, updatedParticipant.attendanceStatus());

        // Other properties should remain the same
        assertEquals(participant.id(), updatedParticipant.id());
        assertEquals(participant.member(), updatedParticipant.member());
        assertEquals(participant.event(), updatedParticipant.event());
        assertEquals(participant.rsvpId(), updatedParticipant.rsvpId());
        assertEquals(participant.isOrganizer(), updatedParticipant.isOrganizer());
        assertEquals(participant.hasEnteredRaffle(), updatedParticipant.hasEnteredRaffle());
        assertEquals(participant.rsvpStatus(), updatedParticipant.rsvpStatus());
    }

    @Test
    void testWithEnteredRaffle() {
        // Given
        ParticipantRecord participant = createSampleParticipant();
        assertFalse(participant.hasEnteredRaffle(), "Initial state should have hasEnteredRaffle = false");

        // When
        ParticipantRecord updatedParticipant = participant.withEnteredRaffle();

        // Then
        assertTrue(updatedParticipant.hasEnteredRaffle(), "Updated participant should have hasEnteredRaffle = true");

        // Other properties should remain the same
        assertEquals(participant.id(), updatedParticipant.id());
        assertEquals(participant.member(), updatedParticipant.member());
        assertEquals(participant.event(), updatedParticipant.event());
        assertEquals(participant.rsvpId(), updatedParticipant.rsvpId());
        assertEquals(participant.isOrganizer(), updatedParticipant.isOrganizer());
        assertEquals(participant.rsvpStatus(), updatedParticipant.rsvpStatus());
        assertEquals(participant.attendanceStatus(), updatedParticipant.attendanceStatus());
    }

    @Test
    void testStatusConversion() {
        // Test JPA to domain model conversion
        assertEquals(ParticipantRecord.AttendanceStatus.ATTENDED,
                Mapper.fromJpaAttendanceStatus(Participant.AttendanceStatus.ATTENDED));
        assertEquals(ParticipantRecord.AttendanceStatus.NO_SHOW,
                Mapper.fromJpaAttendanceStatus(Participant.AttendanceStatus.NO_SHOW));
        assertEquals(ParticipantRecord.AttendanceStatus.UNKNOWN,
                Mapper.fromJpaAttendanceStatus(Participant.AttendanceStatus.UNKNOWN));
        assertEquals(ParticipantRecord.AttendanceStatus.UNKNOWN,
                Mapper.fromJpaAttendanceStatus(null));

        // Test domain model to JPA conversion
        assertEquals(Participant.AttendanceStatus.ATTENDED,
                Mapper.toJpaAttendanceStatus(ParticipantRecord.AttendanceStatus.ATTENDED));
        assertEquals(Participant.AttendanceStatus.NO_SHOW,
                Mapper.toJpaAttendanceStatus(ParticipantRecord.AttendanceStatus.NO_SHOW));
        assertEquals(Participant.AttendanceStatus.UNKNOWN,
                Mapper.toJpaAttendanceStatus(ParticipantRecord.AttendanceStatus.UNKNOWN));
        assertEquals(Participant.AttendanceStatus.UNKNOWN,
                Mapper.toJpaAttendanceStatus(null));
    }

    @Test
    void testRsvpStatusConversion() {
        // Test JPA to domain model conversion
        assertEquals(ParticipantRecord.RsvpStatus.YES,
                Mapper.fromJpaRsvpStatus(Participant.RSVPStatus.YES));
        assertEquals(ParticipantRecord.RsvpStatus.NO,
                Mapper.fromJpaRsvpStatus(Participant.RSVPStatus.NO));
        assertEquals(ParticipantRecord.RsvpStatus.NO,
                Mapper.fromJpaRsvpStatus(null));

        // Test domain model to JPA conversion
        assertEquals(Participant.RSVPStatus.YES,
                Mapper.toJpaRsvpStatus(ParticipantRecord.RsvpStatus.YES));
        assertEquals(Participant.RSVPStatus.NO,
                Mapper.toJpaRsvpStatus(ParticipantRecord.RsvpStatus.NO));
        assertEquals(Participant.RSVPStatus.NO,
                Mapper.toJpaRsvpStatus(null));
    }

    private ParticipantRecord createSampleParticipant() {
        return new ParticipantRecord(
                1L,
                new MemberRecord(1L, "member123", "John Doe", "john@example.com", OffsetDateTime.now()),
                EventRecord.simple(1L, "event123", "Test Event"),
                "rsvp123",
                false,
                false,
                ParticipantRecord.RsvpStatus.YES,
                ParticipantRecord.AttendanceStatus.UNKNOWN
        );
    }
}