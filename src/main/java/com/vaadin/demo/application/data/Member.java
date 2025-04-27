package com.vaadin.demo.application.data;

import com.vaadin.demo.application.services.meetup.MeetupClient;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity to store information about a Meetup member
 * This is independent of any specific event
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Member extends AbstractEntity {

    @Column(unique = true)
    private String meetupId;

    private String name;
    private String email;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated = OffsetDateTime.now();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Participant> participations = new HashSet<>();

    /**
     * Updates the member data from a Meetup API response
     */
    public void updateFromApiResponse(MeetupClient.RSVP apiMember) {
        this.name = apiMember.name();
        this.email = apiMember.email();
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Add this member as a participant to an event
     */
    public Participant participateIn(MeetupEvent event) {
        Participant participant = new Participant();
        participant.setMember(this);
        participant.setMeetupEvent(event);
        participations.add(participant);
        event.getParticipants().add(participant);
        return participant;
    }
}