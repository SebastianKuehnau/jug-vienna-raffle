package com.vaadin.demo.application.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrizeRecordTest {

    @Test
    void testPrizeRecordCreation() {
        // Given
        Long id = 1L;
        String name = "Test Prize";
        ParticipantRecord winner = createSampleParticipant();
        RaffleRecord raffle = createSampleRaffle();

        // When
        String voucherCode = "ABC123";
        LocalDate validUntil = LocalDate.now().plusMonths(3);
        PrizeRecord prizeRecord = new PrizeRecord(id, name, "Prize description", "Template text", false, winner, raffle, voucherCode, validUntil);

        // Then
        assertEquals(id, prizeRecord.id());
        assertEquals(name, prizeRecord.name());
        assertEquals(winner, prizeRecord.winner());
        assertEquals(raffle, prizeRecord.raffle());
        assertEquals(voucherCode, prizeRecord.voucherCode());
        assertEquals(validUntil, prizeRecord.validUntil());
    }

    @Test
    void testSimpleCreation() {
        // Given
        Long id = 1L;
        String name = "Test Prize";

        // When
        PrizeRecord prizeRecord = PrizeRecord.simple(id, name);

        // Then
        assertEquals(id, prizeRecord.id());
        assertEquals(name, prizeRecord.name());
        assertNull(prizeRecord.winner());
        assertNull(prizeRecord.raffle());
    }

    @Test
    void testWithWinner() {
        // Given
        PrizeRecord prizeRecord = new PrizeRecord(1L, "Test Prize", "Description", null, false, null, createSampleRaffle(), "XYZ456", LocalDate.now().plusMonths(6));
        ParticipantRecord winner = createSampleParticipant();

        // When
        PrizeRecord updatedPrize = prizeRecord.withWinner(winner);

        // Then
        assertEquals(winner, updatedPrize.winner());
        
        // Other properties should remain the same
        assertEquals(prizeRecord.id(), updatedPrize.id());
        assertEquals(prizeRecord.name(), updatedPrize.name());
        assertEquals(prizeRecord.raffle(), updatedPrize.raffle());
        assertEquals(prizeRecord.voucherCode(), updatedPrize.voucherCode());
        assertEquals(prizeRecord.validUntil(), updatedPrize.validUntil());
    }

    private ParticipantRecord createSampleParticipant() {
        return new ParticipantRecord(
                1L,
                new MemberRecord(1L, "member123", "John Doe", "john@example.com"),
                EventRecord.simple(1L, "event123", "Test Event"),
                "rsvp123",
                false,
                false,
                ParticipantRecord.RsvpStatus.YES,
                ParticipantRecord.AttendanceStatus.UNKNOWN
        );
    }

    private RaffleRecord createSampleRaffle() {
        return new RaffleRecord(
                1L,
                EventRecord.simple(1L, "event123", "Test Event"),
                "event123",
                List.of()
        );
    }
}