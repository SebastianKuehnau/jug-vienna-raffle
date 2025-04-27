package com.vaadin.demo.application.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

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

    @Column(length = 2000)
    private String description;

    @Column(name = "template")
    private boolean template = false;

    /**
     * Optional voucher code for this prize
     */
    @Column(name = "voucher_code")
    private String voucherCode;

    /**
     * Optional expiration date until when the prize should be redeemed
     */
    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "template_text", length = 4000)
    private String templateText;

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

    /**
     * Create a new prize based on this template
     */
    public Prize createFromTemplate() {
        if (!template) {
            throw new IllegalStateException("Cannot create from a non-template prize");
        }

        Prize prize = new Prize();
        prize.setName(this.getName());
        prize.setDescription(this.getDescription());
        prize.setTemplateText(this.getTemplateText());
        prize.setVoucherCode(this.getVoucherCode());
        prize.setValidUntil(this.getValidUntil());
        prize.setTemplate(false);

        return prize;
    }

    /**
     * Process template text by replacing placeholders with actual values
     */
    public String processTemplateText(String raffleDate, String winnerName, String voucherCode) {
        if (templateText == null) {
            return null;
        }

        String processed = templateText;
        processed = processed.replace("{{PRIZE_NAME}}", name != null ? name : "");
        processed = processed.replace("{{WINNER_NAME}}", winnerName != null ? winnerName : "");
        processed = processed.replace("{{RAFFLE_DATE}}", raffleDate != null ? raffleDate : "");
        processed = processed.replace("{{VOUCHER_CODE}}", voucherCode != null ? voucherCode : "");

        if (validUntil != null) {
            processed = processed.replace("{{VALID_UNTIL}}", validUntil.toString());
        } else {
            processed = processed.replace("{{VALID_UNTIL}}", "");
        }

        return processed;
    }
}