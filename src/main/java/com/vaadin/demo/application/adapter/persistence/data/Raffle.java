package com.vaadin.demo.application.adapter.persistence.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Entity
public class Raffle extends AbstractEntity {

    // Keep for backward compatibility
    private String meetup_event_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private MeetupEvent event;

    @OneToMany(mappedBy = "raffle", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Prize> prizes = new HashSet<>();

    /**
     * Set the event - updates both entity and ID string for compatibility
     */
    public void setEvent(MeetupEvent event) {
        this.event = event;
        if (event != null) {
            this.meetup_event_id = event.getMeetupId();
        }
    }
}
