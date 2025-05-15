package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.adapter.out.persistence.repository.MemberRepository;
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
class MemberServiceAdapterTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceAdapter memberServiceAdapter;

    private Member testMember;
    private MemberRecord testMemberRecord;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        now = OffsetDateTime.now();
        
        testMember = new Member();
        testMember.setId(1L);
        testMember.setMeetupId("member123");
        testMember.setName("John Doe");
        testMember.setEmail("john@example.com");
        testMember.setLastUpdated(now);
        
        testMemberRecord = new MemberRecord(
            testMember.getId(),
            testMember.getMeetupId(),
            testMember.getName(),
            testMember.getEmail(),
            testMember.getLastUpdated()
        );
    }

    @Test
    void getMemberById_shouldReturnMemberRecord() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // When
        Optional<MemberRecord> result = memberServiceAdapter.getMemberById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMemberRecord, result.get());
        verify(memberRepository).findById(1L);
    }

    @Test
    void getMemberById_shouldReturnEmptyWhenNotFound() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<MemberRecord> result = memberServiceAdapter.getMemberById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(memberRepository).findById(999L);
    }

    @Test
    void getMemberByMeetupId_shouldReturnMemberRecord() {
        // Given
        when(memberRepository.findByMeetupId("member123")).thenReturn(Optional.of(testMember));

        // When
        Optional<MemberRecord> result = memberServiceAdapter.getMemberByMeetupId("member123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMemberRecord, result.get());
        verify(memberRepository).findByMeetupId("member123");
    }

    @Test
    void getAllMembers_shouldReturnPageOfMemberRecords() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> memberPage = new PageImpl<>(List.of(testMember), pageable, 1);
        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        // When
        Page<MemberRecord> result = memberServiceAdapter.getAllMembers(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(testMemberRecord, result.getContent().get(0));
        verify(memberRepository).findAll(pageable);
    }

    @Test
    void getMembers_shouldReturnFilteredMembers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Member> specification = (root, query, cb) -> cb.equal(root.get("meetupId"), "member123");
        Page<Member> memberPage = new PageImpl<>(List.of(testMember), pageable, 1);
        when(memberRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(memberPage);

        // When
        Page<MemberRecord> result = memberServiceAdapter.getMembers(pageable, specification);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(testMemberRecord, result.getContent().get(0));
        verify(memberRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void saveMember_shouldCreateNewMember() {
        // Given
        MemberRecord newMemberRecord = new MemberRecord(
            null,
            "new123",
            "New Member",
            "new@example.com",
            null
        );
        
        Member newMember = new Member();
        newMember.setMeetupId("new123");
        newMember.setName("New Member");
        newMember.setEmail("new@example.com");
        
        Member savedMember = new Member();
        savedMember.setId(2L);
        savedMember.setMeetupId("new123");
        savedMember.setName("New Member");
        savedMember.setEmail("new@example.com");
        savedMember.setLastUpdated(now);
        
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        MemberRecord result = memberServiceAdapter.saveMember(newMemberRecord);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("new123", result.meetupId());
        assertEquals("New Member", result.name());
        assertEquals("new@example.com", result.email());
        assertNotNull(result.lastUpdated());
        
        verify(memberRepository).save(any(Member.class));
        verify(memberRepository, never()).findById(any());
    }

    @Test
    void saveMember_shouldUpdateExistingMember() {
        // Given
        MemberRecord updatedMemberRecord = new MemberRecord(
            1L,
            "member123",
            "Updated Name",
            "updated@example.com",
            now
        );
        
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setMeetupId("member123");
        updatedMember.setName("Updated Name");
        updatedMember.setEmail("updated@example.com");
        updatedMember.setLastUpdated(now);
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        // When
        MemberRecord result = memberServiceAdapter.saveMember(updatedMemberRecord);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("member123", result.meetupId());
        assertEquals("Updated Name", result.name());
        assertEquals("updated@example.com", result.email());
        
        verify(memberRepository).findById(1L);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void saveMember_shouldCreateNewMemberWhenIdExistsButNotFound() {
        // Given
        MemberRecord memberRecord = new MemberRecord(
            999L,
            "notfound123",
            "Not Found",
            "notfound@example.com",
            now
        );
        
        Member savedMember = new Member();
        savedMember.setId(999L);
        savedMember.setMeetupId("notfound123");
        savedMember.setName("Not Found");
        savedMember.setEmail("notfound@example.com");
        savedMember.setLastUpdated(now);
        
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        MemberRecord result = memberServiceAdapter.saveMember(memberRecord);

        // Then
        assertNotNull(result);
        assertEquals(999L, result.id());
        
        verify(memberRepository).findById(999L);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void deleteMember_shouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(memberRepository).deleteById(1L);

        // When
        memberServiceAdapter.deleteMember(1L);

        // Then
        verify(memberRepository).deleteById(1L);
    }

    @Test
    void countMembers_shouldReturnMemberCount() {
        // Given
        when(memberRepository.count()).thenReturn(10L);

        // When
        int result = memberServiceAdapter.countMembers();

        // Then
        assertEquals(10, result);
        verify(memberRepository).count();
    }
}