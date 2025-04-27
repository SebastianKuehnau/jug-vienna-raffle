package com.vaadin.demo.application.domain.model;

import java.time.OffsetDateTime;

/**
 * Immutable domain object representing a member
 */
public record MemberRecord(
    Long id,
    String meetupId,
    String name,
    String email,
    OffsetDateTime lastUpdated
) {
    /**
     * Create a simplified version with only essential info
     */
    public static MemberRecord simple(Long id, String meetupId, String name) {
        return new MemberRecord(id, meetupId, name, null, null);
    }
}