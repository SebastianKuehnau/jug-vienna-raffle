package com.vaadin.demo.application.adapter.out.persistence.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity to store Meetup event information
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class MeetupEvent extends AbstractEntity {

    @Column(unique = true)
    private String meetupId;

    private String token;
    private String title;

    @Column(length = 5000)
    private String description;

    private OffsetDateTime dateTime;
    private String eventUrl;
    private String status;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated = OffsetDateTime.now();

    @OneToMany(mappedBy = "meetupEvent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Participant> participants = new HashSet<>();

    /**
     * Adds a participant to this event
     */
    public Participant addParticipant(Member member) {
        return member.participateIn(this);
    }

}