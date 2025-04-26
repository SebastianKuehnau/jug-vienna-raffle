package com.vaadin.demo.application.domain.model;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void testToEventRecord() {
        // Given
        MeetupEvent event = new MeetupEvent();
        event.setId(1L);
        event.setMeetupId("event123");
        event.setTitle("Test Event");
        event.setDescription("Description");
        OffsetDateTime now = OffsetDateTime.now();
        event.setDateTime(now);
        event.setEventUrl("http://event.url");
        event.setStatus("active");

        // When
        EventRecord eventRecord = Mapper.toEventRecord(event);

        // Then
        assertEquals(event.getId(), eventRecord.id());
        assertEquals(event.getMeetupId(), eventRecord.meetupId());
        assertEquals(event.getTitle(), eventRecord.title());
        assertEquals(event.getDescription(), eventRecord.description());
        assertEquals(event.getDateTime(), eventRecord.eventDate());
        assertNull(eventRecord.venue()); // No venue in original entity
        assertEquals(event.getEventUrl(), eventRecord.link());
    }

    @Test
    void testToMemberRecord() {
        // Given
        Member member = new Member();
        member.setId(1L);
        member.setMeetupId("member123");
        member.setName("John Doe");
        member.setEmail("john@example.com");

        // When
        MemberRecord memberRecord = Mapper.toMemberRecord(member);

        // Then
        assertEquals(member.getId(), memberRecord.id());
        assertEquals(member.getMeetupId(), memberRecord.meetupId());
        assertEquals(member.getName(), memberRecord.name());
        assertEquals(member.getEmail(), memberRecord.email());
    }

    @Test
    void testToParticipantRecord() {
        // Given
        Member member = new Member();
        member.setId(1L);
        member.setMeetupId("member123");
        member.setName("John Doe");
        member.setEmail("john@example.com");

        MeetupEvent event = new MeetupEvent();
        event.setId(1L);
        event.setMeetupId("event123");
        event.setTitle("Test Event");

        Participant participant = new Participant();
        participant.setId(1L);
        participant.setMember(member);
        participant.setMeetupEvent(event);
        participant.setRsvpId("rsvp123");
        participant.setIsOrganizer(false);
        participant.setHasEnteredRaffle(false);
        participant.setRsvpStatus(Participant.RSVPStatus.YES);
        participant.setAttendanceStatus(Participant.AttendanceStatus.UNKNOWN);

        // When
        ParticipantRecord participantRecord = Mapper.toParticipantRecord(participant);

        // Then
        assertEquals(participant.getId(), participantRecord.id());
        assertNotNull(participantRecord.member());
        assertEquals(member.getId(), participantRecord.member().id());
        assertNotNull(participantRecord.event());
        assertEquals(event.getId(), participantRecord.event().id());
        assertEquals(participant.getRsvpId(), participantRecord.rsvpId());
        assertEquals(participant.getIsOrganizer(), participantRecord.isOrganizer());
        assertEquals(participant.getHasEnteredRaffle(), participantRecord.hasEnteredRaffle());
        assertEquals(ParticipantRecord.RsvpStatus.YES, participantRecord.rsvpStatus());
        assertEquals(ParticipantRecord.AttendanceStatus.UNKNOWN, participantRecord.attendanceStatus());
    }

    @Test
    void testToPrizeRecord() {
        // Given
        MeetupEvent event = new MeetupEvent();
        event.setId(1L);
        event.setMeetupId("event123");
        event.setTitle("Test Event");

        Raffle raffle = new Raffle();
        raffle.setId(1L);
        raffle.setEvent(event);
        raffle.setMeetup_event_id(event.getMeetupId());

        Member member = new Member();
        member.setId(1L);
        member.setMeetupId("member123");
        member.setName("John Doe");

        Participant winner = new Participant();
        winner.setId(1L);
        winner.setMember(member);
        winner.setMeetupEvent(event);

        Prize prize = new Prize();
        prize.setId(1L);
        prize.setName("Test Prize");
        prize.setRaffle(raffle);
        prize.setWinner(winner);

        // When
        PrizeRecord prizeRecord = Mapper.toPrizeRecord(prize);

        // Then
        assertEquals(prize.getId(), prizeRecord.id());
        assertEquals(prize.getName(), prizeRecord.name());
        assertNotNull(prizeRecord.winner());
        assertEquals(winner.getId(), prizeRecord.winner().id());
        assertNotNull(prizeRecord.raffle());
        assertEquals(raffle.getId(), prizeRecord.raffle().id());
    }

    @Test
    void testToRaffleRecord() {
        // Given
        MeetupEvent event = new MeetupEvent();
        event.setId(1L);
        event.setMeetupId("event123");
        event.setTitle("Test Event");

        Raffle raffle = new Raffle();
        raffle.setId(1L);
        raffle.setEvent(event);
        raffle.setMeetup_event_id(event.getMeetupId());

        Member member = new Member();
        member.setId(1L);
        member.setMeetupId("member123");
        member.setName("John Doe");

        Participant winner = new Participant();
        winner.setId(1L);
        winner.setMember(member);
        winner.setMeetupEvent(event);

        Prize prize1 = new Prize();
        prize1.setId(1L);
        prize1.setName("Prize 1");
        prize1.setRaffle(raffle);
        prize1.setWinner(winner);

        Prize prize2 = new Prize();
        prize2.setId(2L);
        prize2.setName("Prize 2");
        prize2.setRaffle(raffle);

        raffle.setPrizes(Set.of(prize1, prize2));

        // When
        RaffleRecord raffleRecord = Mapper.toRaffleRecord(raffle);

        // Then
        assertEquals(raffle.getId(), raffleRecord.id());
        assertNotNull(raffleRecord.event());
        assertEquals(event.getId(), raffleRecord.event().id());
        assertEquals(raffle.getMeetup_event_id(), raffleRecord.meetupId());
        assertNotNull(raffleRecord.prizes());
        assertEquals(2, raffleRecord.prizes().size());
    }

    @Test
    void testToRaffleRecordWithoutPrizes() {
        // Given
        MeetupEvent event = new MeetupEvent();
        event.setId(1L);
        event.setMeetupId("event123");
        event.setTitle("Test Event");

        Raffle raffle = new Raffle();
        raffle.setId(1L);
        raffle.setEvent(event);
        raffle.setMeetup_event_id(event.getMeetupId());
        raffle.setPrizes(Set.of());

        // When
        RaffleRecord raffleRecord = Mapper.toRaffleRecord(raffle, false);

        // Then
        assertEquals(raffle.getId(), raffleRecord.id());
        assertNotNull(raffleRecord.event());
        assertEquals(event.getId(), raffleRecord.event().id());
        assertEquals(raffle.getMeetup_event_id(), raffleRecord.meetupId());
        assertNotNull(raffleRecord.prizes());
        assertTrue(raffleRecord.prizes().isEmpty(), "Prizes should be empty when includePrizes is false");
    }
}