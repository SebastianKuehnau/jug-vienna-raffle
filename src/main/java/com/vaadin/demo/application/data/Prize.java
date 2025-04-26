package com.vaadin.demo.application.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Entity
public class Prize extends AbstractEntity {

    private String name;

    // Keep the string winner for backward compatibility
    private String winnerName;
    
    @OneToOne
    @JoinColumn(name = "winner_id")
    private Participant winner;

    @ManyToOne
    @JoinColumn(name = "raffle_id")
    @ToString.Exclude
    private Raffle raffle;
    
    /**
     * Update the winner name from the participant
     */
    public void setWinner(Participant winner) {
        this.winner = winner;
        if (winner != null && winner.getMember() != null) {
            this.winnerName = winner.getMember().getName();
        } else {
            this.winnerName = null;
        }
    }
}
