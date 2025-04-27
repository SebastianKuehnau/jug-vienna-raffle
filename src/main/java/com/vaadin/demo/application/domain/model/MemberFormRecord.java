package com.vaadin.demo.application.domain.model;

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
     * Create a new MemberFormRecord from a MemberRecord
     */
    public static MemberFormRecord fromMemberRecord(MemberRecord memberRecord) {
        if (memberRecord == null) return null;
        
        return new MemberFormRecord(
            memberRecord.id(),
            memberRecord.meetupId(),
            memberRecord.name(),
            memberRecord.email(),
            null // lastUpdated is not in MemberRecord
        );
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
        return new MemberFormRecord(null, null, "", "", null);
    }
    
    /**
     * Convert to a MemberRecord
     */
    public MemberRecord toMemberRecord() {
        return new MemberRecord(
            this.id,
            this.meetupId,
            this.name,
            this.email
        );
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
    
    /**
     * Create a new form with updated meetupId
     */
    public MemberFormRecord withMeetupId(String meetupId) {
        return new MemberFormRecord(this.id, meetupId, this.name, this.email, this.lastUpdated);
    }
    
    /**
     * Create a new form with updated lastUpdated
     */
    public MemberFormRecord withLastUpdated(OffsetDateTime lastUpdated) {
        return new MemberFormRecord(this.id, this.meetupId, this.name, this.email, lastUpdated);
    }
}