package com.vaadin.demo.application.domain.model;

import java.time.LocalDate;

/**
 * Immutable domain object representing a prize template
 */
public record PrizeTemplateRecord(
    Long id,
    String name,
    String description,
    String templateText,
    String voucherCode,
    LocalDate validUntil
) {
    /**
     * Create a prize record from this template
     */
    public PrizeRecord toPrizeRecord(RaffleRecord raffle) {
        return new PrizeRecord(
            null,
            this.name,
            this.description,
            this.templateText,
            null,
            raffle,
            this.voucherCode,
            this.validUntil
        );
    }

    /**
     * Create a simple template with minimal details
     */
    public static PrizeTemplateRecord simple(Long id, String name) {
        return new PrizeTemplateRecord(id, name, null, null, null, null);
    }
}