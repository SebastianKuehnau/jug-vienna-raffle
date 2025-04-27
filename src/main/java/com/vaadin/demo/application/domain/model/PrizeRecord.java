package com.vaadin.demo.application.domain.model;

import java.time.LocalDate;

/**
 * Immutable domain object representing a prize
 */
public record PrizeRecord(
    Long id,
    String name,
    String description,
    String templateText,
    boolean isTemplate,
    ParticipantRecord winner,
    RaffleRecord raffle,
    String voucherCode,
    LocalDate validUntil
) {
    /**
     * Create a prize with updated winner
     */
    public PrizeRecord withWinner(ParticipantRecord winner) {
        return new PrizeRecord(
            this.id,
            this.name,
            this.description,
            this.templateText,
            this.isTemplate,
            winner,
            this.raffle,
            this.voucherCode,
            this.validUntil
        );
    }
    
    /**
     * Create a simple prize with minimal details 
     */
    public static PrizeRecord simple(Long id, String name) {
        return new PrizeRecord(id, name, null, null, false, null, null, null, null);
    }
    
    /**
     * Create a template prize
     */
    public static PrizeRecord template(Long id, String name, String description, String templateText) {
        return new PrizeRecord(id, name, description, templateText, true, null, null, null, null);
    }
    
    /**
     * Create a prize from template
     */
    public static PrizeRecord fromTemplate(PrizeRecord template, RaffleRecord raffle) {
        if (!template.isTemplate) {
            throw new IllegalArgumentException("Cannot create from a non-template prize");
        }
        
        return new PrizeRecord(
            null,
            template.name,
            template.description,
            template.templateText,
            false,
            null,
            raffle,
            template.voucherCode,
            template.validUntil
        );
    }
    
    /**
     * Process template text by replacing placeholders with actual values
     */
    public String processedText(String raffleDate, String voucherCode) {
        if (templateText == null) {
            return null;
        }
        
        String processed = templateText;
        processed = processed.replace("{{PRIZE_NAME}}", name != null ? name : "");
        processed = processed.replace("{{WINNER_NAME}}", 
            winner != null && winner.member() != null ? winner.member().name() : "");
        processed = processed.replace("{{RAFFLE_DATE}}", raffleDate != null ? raffleDate : "");
        processed = processed.replace("{{VOUCHER_CODE}}", voucherCode != null ? voucherCode : "");
        
        if (validUntil != null) {
            processed = processed.replace("{{VALID_UNTIL}}", validUntil.toString());
        } else {
            processed = processed.replace("{{VALID_UNTIL}}", "");
        }
        
        return processed;
    }
    
    /**
     * Backward compatibility constructor for code that does not yet support the new fields
     */
    public PrizeRecord(Long id, String name, String description, String templateText, 
                       boolean isTemplate, ParticipantRecord winner, RaffleRecord raffle) {
        this(id, name, description, templateText, isTemplate, winner, raffle, null, null);
    }
}