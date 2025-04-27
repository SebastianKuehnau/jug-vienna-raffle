package com.vaadin.demo.application.domain.port;

import com.vaadin.demo.application.domain.model.MemberRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

/**
 * Port for Member operations
 * This defines the interface for interacting with Member-related functionality
 */
public interface MemberPort {
    
    /**
     * Get a member by ID
     */
    Optional<MemberRecord> getMemberById(Long id);
    
    /**
     * Get a member by Meetup ID
     */
    Optional<MemberRecord> getMemberByMeetupId(String meetupId);
    
    /**
     * Get all members with pagination
     */
    Page<MemberRecord> getAllMembers(Pageable pageable);
    
    /**
     * Get members matching a specification with pagination
     */
    Page<MemberRecord> getMembers(Pageable pageable, Specification<?> specification);
    
    /**
     * Save a member
     */
    MemberRecord saveMember(MemberRecord member);
    
    /**
     * Delete a member by ID
     */
    void deleteMember(Long id);
    
    /**
     * Count all members
     */
    int countMembers();
}