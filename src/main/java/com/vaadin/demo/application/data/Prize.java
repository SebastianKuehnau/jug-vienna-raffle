package com.vaadin.demo.application.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Prize extends AbstractEntity {

    @ToString.Include
    private String name;

    // Keep the string winner for backward compatibility
    @ToString.Include
    private String winnerName;
    
    @OneToOne
    @JoinColumn(name = "winner_id")
    @ToString.Exclude
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
