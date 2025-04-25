package com.vaadin.demo.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Participant extends AbstractEntity {

    private String memberId;
    private String eventId;

    @OneToOne
    @JoinColumn(name = "winner_of_id")
    private Prize winnerOf;

}
