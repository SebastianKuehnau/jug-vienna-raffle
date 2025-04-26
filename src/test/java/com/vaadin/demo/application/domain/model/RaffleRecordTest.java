package com.vaadin.demo.application.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RaffleRecordTest {

    @Test
    void testRaffleRecordCreation() {
        // Given
        Long id = 1L;
        EventRecord event = EventRecord.simple(1L, "event123", "Test Event");
        String meetupId = "event123";
        List<PrizeRecord> prizes = List.of(
                PrizeRecord.simple(1L, "Prize 1"),
                PrizeRecord.simple(2L, "Prize 2")
        );

        // When
        RaffleRecord raffleRecord = new RaffleRecord(id, event, meetupId, prizes);

        // Then
        assertEquals(id, raffleRecord.id());
        assertEquals(event, raffleRecord.event());
        assertEquals(meetupId, raffleRecord.meetupId());
        assertEquals(prizes, raffleRecord.prizes());
        assertEquals(2, raffleRecord.prizes().size());
    }

    @Test
    void testSimpleCreation() {
        // Given
        Long id = 1L;
        EventRecord event = EventRecord.simple(1L, "event123", "Test Event");
        String meetupId = "event123";

        // When
        RaffleRecord raffleRecord = RaffleRecord.simple(id, event, meetupId);

        // Then
        assertEquals(id, raffleRecord.id());
        assertEquals(event, raffleRecord.event());
        assertEquals(meetupId, raffleRecord.meetupId());
        assertNotNull(raffleRecord.prizes());
        assertTrue(raffleRecord.prizes().isEmpty());
    }

    @Test
    void testImmutabilityOfPrizesList() {
        // Given
        RaffleRecord raffleRecord = RaffleRecord.simple(1L, 
                EventRecord.simple(1L, "event123", "Test Event"), "event123");
        
        // Then - verify that prizes list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            raffleRecord.prizes().add(PrizeRecord.simple(1L, "Prize"));
        });
    }
}