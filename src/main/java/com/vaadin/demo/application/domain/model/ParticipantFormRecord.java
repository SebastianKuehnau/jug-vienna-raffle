package com.vaadin.demo.application.domain.model;

import java.time.OffsetDateTime;

/**
 * Immutable domain object for Participant form data
 * Used in the UI layer to prevent direct dependency on JPA entities
 */
public record ParticipantFormRecord(
    Long id,
    Long memberId,
    String memberName,
    String memberEmail,
    Long eventId,
    String eventTitle,
    String rsvpId,
    boolean isOrganizer,
    boolean hasEnteredRaffle,
    String rsvpStatus,
    String attendanceStatus,
    OffsetDateTime lastUpdated
) {
    /**
     * Enum for RSVP status
     */
    public enum RsvpStatus {
        YES, NO, WAITLIST
    }
    
    /**
     * Enum for attendance status
     */
    public enum AttendanceStatus {
        UNKNOWN, ATTENDED, NO_SHOW
    }
    
    /**
     * Create a new ParticipantFormRecord from a ParticipantRecord
     */
    public static ParticipantFormRecord fromParticipantRecord(ParticipantRecord participantRecord) {
        if (participantRecord == null) return null;
        
        MemberRecord member = participantRecord.member();
        EventRecord event = participantRecord.event();
        
        return new ParticipantFormRecord(
            participantRecord.id(),
            member != null ? member.id() : null,
            member != null ? member.name() : null,
            member != null ? member.email() : null,
            event != null ? event.id() : null,
            event != null ? event.title() : null,
            participantRecord.rsvpId(),
            participantRecord.isOrganizer(),
            participantRecord.hasEnteredRaffle(),
            participantRecord.rsvpStatus() != null ? participantRecord.rsvpStatus().name() : null,
            participantRecord.attendanceStatus() != null ? participantRecord.attendanceStatus().name() : null,
            null // lastUpdated is not in ParticipantRecord
        );
    }
    
    /**
     * Create a simple form record with minimal details
     */
    public static ParticipantFormRecord simple(Long id, String memberName) {
        return new ParticipantFormRecord(id, null, memberName, null, null, null, null, false, false, RsvpStatus.YES.name(), AttendanceStatus.UNKNOWN.name(), null);
    }
    
    /**
     * Create an empty form record
     */
    public static ParticipantFormRecord empty() {
        return new ParticipantFormRecord(null, null, "", "", null, "", null, false, false, RsvpStatus.YES.name(), AttendanceStatus.UNKNOWN.name(), null);
    }
    
    /**
     * Convert to a ParticipantRecord
     */
    public ParticipantRecord toParticipantRecord(MemberRecord memberRecord, EventRecord eventRecord) {
        ParticipantRecord.RsvpStatus rsvpStatusEnum = null;
        if (this.rsvpStatus != null) {
            try {
                rsvpStatusEnum = ParticipantRecord.RsvpStatus.valueOf(this.rsvpStatus);
            } catch (IllegalArgumentException e) {
                rsvpStatusEnum = ParticipantRecord.RsvpStatus.YES;
            }
        }
        
        ParticipantRecord.AttendanceStatus attendanceStatusEnum = null;
        if (this.attendanceStatus != null) {
            try {
                attendanceStatusEnum = ParticipantRecord.AttendanceStatus.valueOf(this.attendanceStatus);
            } catch (IllegalArgumentException e) {
                attendanceStatusEnum = ParticipantRecord.AttendanceStatus.UNKNOWN;
            }
        }
        
        return new ParticipantRecord(
            this.id,
            memberRecord,
            eventRecord,
            this.rsvpId,
            this.isOrganizer,
            this.hasEnteredRaffle,
            rsvpStatusEnum,
            attendanceStatusEnum
        );
    }
    
    /**
     * Create a new form with updated member information
     */
    public ParticipantFormRecord withMember(Long memberId, String memberName, String memberEmail) {
        return new ParticipantFormRecord(
            this.id, memberId, memberName, memberEmail, this.eventId, this.eventTitle,
            this.rsvpId, this.isOrganizer, this.hasEnteredRaffle,
            this.rsvpStatus, this.attendanceStatus, this.lastUpdated
        );
    }
    
    /**
     * Create a new form with updated event information
     */
    public ParticipantFormRecord withEvent(Long eventId, String eventTitle) {
        return new ParticipantFormRecord(
            this.id, this.memberId, this.memberName, this.memberEmail, eventId, eventTitle,
            this.rsvpId, this.isOrganizer, this.hasEnteredRaffle,
            this.rsvpStatus, this.attendanceStatus, this.lastUpdated
        );
    }
    
    /**
     * Create a new form with updated raffle status
     */
    public ParticipantFormRecord withRaffleStatus(boolean hasEnteredRaffle) {
        return new ParticipantFormRecord(
            this.id, this.memberId, this.memberName, this.memberEmail, this.eventId, this.eventTitle,
            this.rsvpId, this.isOrganizer, hasEnteredRaffle,
            this.rsvpStatus, this.attendanceStatus, this.lastUpdated
        );
    }
    
    /**
     * Create a new form with updated attendance status
     */
    public ParticipantFormRecord withAttendanceStatus(String attendanceStatus) {
        return new ParticipantFormRecord(
            this.id, this.memberId, this.memberName, this.memberEmail, this.eventId, this.eventTitle,
            this.rsvpId, this.isOrganizer, this.hasEnteredRaffle,
            this.rsvpStatus, attendanceStatus, this.lastUpdated
        );
    }
}