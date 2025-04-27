package com.vaadin.demo.application.domain.model;

import com.vaadin.demo.application.data.Member;

import java.time.OffsetDateTime;

/**
 * Immutable domain object for Member form data
 * Used in the UI layer to prevent direct dependency on JPA entities
 */
public record MemberFormRecord(
    Long id,
    String meetupId,
    String name,
    String email,
    OffsetDateTime lastUpdated
) {
    /**
     * Create a new MemberFormRecord from a Member JPA entity
     */
    public static MemberFormRecord fromMember(Member member) {
        if (member == null) return null;
        
        return new MemberFormRecord(
            member.getId(),
            member.getMeetupId(),
            member.getName(),
            member.getEmail(),
            member.getLastUpdated()
        );
    }
    
    /**
     * Convert to a Member JPA entity
     * Note: This doesn't handle the participations collection
     */
    public Member toMember() {
        Member member = new Member();
        member.setId(this.id);
        member.setMeetupId(this.meetupId);
        member.setName(this.name);
        member.setEmail(this.email);
        member.setLastUpdated(this.lastUpdated != null ? this.lastUpdated : OffsetDateTime.now());
        return member;
    }
    
    /**
     * Update an existing Member entity with values from this form
     */
    public Member updateMember(Member existingMember) {
        existingMember.setMeetupId(this.meetupId);
        existingMember.setName(this.name);
        existingMember.setEmail(this.email);
        existingMember.setLastUpdated(OffsetDateTime.now());
        return existingMember;
    }
    
    /**
     * Create a simple form record with minimal details
     */
    public static MemberFormRecord simple(Long id, String name) {
        return new MemberFormRecord(id, null, name, null, null);
    }
    
    /**
     * Create an empty form record
     */
    public static MemberFormRecord empty() {
        return new MemberFormRecord(null, "", "", "", OffsetDateTime.now());
    }
    
    /**
     * Create a new form with updated meetupId
     */
    public MemberFormRecord withMeetupId(String meetupId) {
        return new MemberFormRecord(this.id, meetupId, this.name, this.email, this.lastUpdated);
    }
    
    /**
     * Create a new form with updated name
     */
    public MemberFormRecord withName(String name) {
        return new MemberFormRecord(this.id, this.meetupId, name, this.email, this.lastUpdated);
    }
    
    /**
     * Create a new form with updated email
     */
    public MemberFormRecord withEmail(String email) {
        return new MemberFormRecord(this.id, this.meetupId, this.name, email, this.lastUpdated);
    }
}