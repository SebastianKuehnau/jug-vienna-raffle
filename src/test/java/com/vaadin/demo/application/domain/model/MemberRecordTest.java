package com.vaadin.demo.application.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberRecordTest {

    @Test
    void testMemberRecordCreation() {
        // Given
        Long id = 1L;
        String meetupId = "member123";
        String name = "John Doe";
        String email = "john@example.com";

        // When
        MemberRecord memberRecord = new MemberRecord(id, meetupId, name, email);

        // Then
        assertEquals(id, memberRecord.id());
        assertEquals(meetupId, memberRecord.meetupId());
        assertEquals(name, memberRecord.name());
        assertEquals(email, memberRecord.email());
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
    }

    @Test
    void testMemberRecordEquality() {
        // Given
        MemberRecord member1 = new MemberRecord(1L, "member123", "John Doe", "john@example.com");
        MemberRecord member2 = new MemberRecord(1L, "member123", "John Doe", "john@example.com");
        MemberRecord member3 = new MemberRecord(2L, "member123", "John Doe", "john@example.com");

        // Then
        assertEquals(member1, member2, "Equal members should be equal");
        assertNotEquals(member1, member3, "Members with different IDs should not be equal");
    }
}