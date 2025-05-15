package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.domain.model.MemberFormRecord;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.application.port.out.MemberPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberApplicationServiceTest {

    @Mock
    private MemberPort memberPort;

    @InjectMocks
    private MemberApplicationServiceImpl memberApplicationService;

    private MemberRecord testMemberRecord;
    private MemberFormRecord testMemberFormRecord;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        now = OffsetDateTime.now();

        testMemberRecord = new MemberRecord(
            1L,
            "member123",
            "John Doe",
            "john@example.com",
            now
        );

        testMemberFormRecord = new MemberFormRecord(
            1L,
            "member123",
            "John Doe",
            "john@example.com",
            now
        );
    }

    @Test
    void getMemberFormById_shouldReturnMemberFormRecord() {
        // Given
        when(memberPort.getMemberById(1L)).thenReturn(Optional.of(testMemberRecord));

        // When
        Optional<MemberFormRecord> result = memberApplicationService.getMemberFormById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMemberFormRecord, result.get());
        verify(memberPort).getMemberById(1L);
    }

    @Test
    void getMemberFormById_shouldReturnEmptyWhenNotFound() {
        // Given
        when(memberPort.getMemberById(999L)).thenReturn(Optional.empty());

        // When
        Optional<MemberFormRecord> result = memberApplicationService.getMemberFormById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(memberPort).getMemberById(999L);
    }

    @Test
    void getMemberFormByMeetupId_shouldReturnMemberFormRecord() {
        // Given
        when(memberPort.getMemberByMeetupId("member123")).thenReturn(Optional.of(testMemberRecord));

        // When
        Optional<MemberFormRecord> result = memberApplicationService.getMemberFormByMeetupId("member123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMemberFormRecord, result.get());
        verify(memberPort).getMemberByMeetupId("member123");
    }

    @Test
    void listMemberForms_shouldReturnPageOfMemberFormRecords() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberRecord> memberRecordPage = new PageImpl<>(List.of(testMemberRecord), pageable, 1);
        when(memberPort.getAllMembers(pageable)).thenReturn(memberRecordPage);

        // When
        Page<MemberFormRecord> result = memberApplicationService.listMemberForms(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(testMemberFormRecord, result.getContent().get(0));
        verify(memberPort).getAllMembers(pageable);
    }

    @Test
    void listMemberForms_withSpecification_shouldReturnFilteredMembers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Member> specification = (root, query, cb) -> cb.equal(root.get("meetupId"), "member123");
        Page<MemberRecord> memberRecordPage = new PageImpl<>(List.of(testMemberRecord), pageable, 1);
        when(memberPort.getMembers(eq(pageable), any())).thenReturn(memberRecordPage);

        // When
        Page<MemberFormRecord> result = memberApplicationService.listMemberForms(pageable, specification);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(testMemberFormRecord, result.getContent().get(0));
        verify(memberPort).getMembers(eq(pageable), any());
    }

    @Test
    void saveMemberForm_shouldSaveMemberRecord() {
        // Given
        MemberFormRecord newMemberForm = new MemberFormRecord(
            null,
            "new123",
            "New Member",
            "new@example.com",
            now
        );

        MemberRecord newMemberRecord = new MemberRecord(
            null,
            "new123",
            "New Member",
            "new@example.com",
            now
        );

        MemberRecord savedMemberRecord = new MemberRecord(
            2L,
            "new123",
            "New Member",
            "new@example.com",
            now
        );

        when(memberPort.saveMember(any(MemberRecord.class))).thenReturn(savedMemberRecord);

        // When
        MemberFormRecord result = memberApplicationService.saveMemberForm(newMemberForm);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("new123", result.meetupId());
        assertEquals("New Member", result.name());
        assertEquals("new@example.com", result.email());

        verify(memberPort).saveMember(argThat(record ->
            record.meetupId().equals(newMemberRecord.meetupId()) &&
            record.name().equals(newMemberRecord.name()) &&
            record.email().equals(newMemberRecord.email())
        ));
    }

    @Test
    void saveMemberForm_shouldUpdateExistingMember() {
        // Given
        MemberFormRecord updatedMemberForm = new MemberFormRecord(
            1L,
            "member123",
            "Updated Name",
            "updated@example.com",
            now
        );

        MemberRecord updatedMemberRecord = new MemberRecord(
            1L,
            "member123",
            "Updated Name",
            "updated@example.com",
            now
        );

        when(memberPort.saveMember(any(MemberRecord.class))).thenReturn(updatedMemberRecord);

        // When
        MemberFormRecord result = memberApplicationService.saveMemberForm(updatedMemberForm);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("member123", result.meetupId());
        assertEquals("Updated Name", result.name());
        assertEquals("updated@example.com", result.email());

        verify(memberPort).saveMember(argThat(record ->
            record.id().equals(updatedMemberRecord.id()) &&
            record.meetupId().equals(updatedMemberRecord.meetupId()) &&
            record.name().equals(updatedMemberRecord.name()) &&
            record.email().equals(updatedMemberRecord.email())
        ));
    }

    @Test
    void deleteMember_shouldCallPortDeleteMember() {
        // Given
        doNothing().when(memberPort).deleteMember(1L);

        // When
        memberApplicationService.deleteMember(1L);

        // Then
        verify(memberPort).deleteMember(1L);
    }

    @Test
    void countMembers_shouldReturnMemberCount() {
        // Given
        when(memberPort.countMembers()).thenReturn(10);

        // When
        int result = memberApplicationService.countMembers();

        // Then
        assertEquals(10, result);
        verify(memberPort).countMembers();
    }
}