package com.vaadin.demo.application.domain.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MemberRecordTest {

    @Test
    void testMemberRecordCreation() {
        // Given
        Long id = 1L;
        String meetupId = "member123";
        String name = "John Doe";
        String email = "john@example.com";
        OffsetDateTime lastUpdated = OffsetDateTime.now();

        // When
        MemberRecord memberRecord = new MemberRecord(id, meetupId, name, email, lastUpdated);

        // Then
        assertEquals(id, memberRecord.id());
        assertEquals(meetupId, memberRecord.meetupId());
        assertEquals(name, memberRecord.name());
        assertEquals(email, memberRecord.email());
        assertEquals(lastUpdated, memberRecord.lastUpdated());
    }

    @Test
    void testMemberRecordSimpleCreation() {
        // Given
        Long id = 1L;
        String meetupId = "member123";
        String name = "John Doe";

        // When
        MemberRecord memberRecord = MemberRecord.simple(id, meetupId, name);

        // Then
        assertEquals(id, memberRecord.id());
        assertEquals(meetupId, memberRecord.meetupId());
        assertEquals(name, memberRecord.name());
        assertNull(memberRecord.email());
        assertNull(memberRecord.lastUpdated());
    }

    @Test
    void testMemberRecordEquality() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        MemberRecord member1 = new MemberRecord(1L, "member123", "John Doe", "john@example.com", now);
        MemberRecord member2 = new MemberRecord(1L, "member123", "John Doe", "john@example.com", now);
        MemberRecord member3 = new MemberRecord(2L, "member123", "John Doe", "john@example.com", now);

        // Then
        assertEquals(member1, member2, "Equal members should be equal");
        assertNotEquals(member1, member3, "Members with different IDs should not be equal");
    }
}