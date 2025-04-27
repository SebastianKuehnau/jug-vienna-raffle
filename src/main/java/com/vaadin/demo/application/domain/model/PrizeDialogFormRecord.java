package com.vaadin.demo.application.domain.model;

import java.time.LocalDate;

/**
 * Immutable domain object for Prize dialog form data
 * Used in the UI for the PrizeDialog component to prevent direct dependency on JPA entities
 */
public record PrizeDialogFormRecord(
    Long id,
    String name,
    String description,
    String templateText,
    String voucherCode,
    LocalDate validUntil,
    String winnerName,
    boolean isTemplate
) {
    /**
     * Create a new PrizeDialogFormRecord from a PrizeRecord
     */
    public static PrizeDialogFormRecord fromPrizeRecord(PrizeRecord prizeRecord) {
        if (prizeRecord == null) {
            return null;
        }

        String winnerName = null;
        if (prizeRecord.winner() != null && prizeRecord.winner().member() != null) {
            winnerName = prizeRecord.winner().member().name();
        }

        return new PrizeDialogFormRecord(
            prizeRecord.id(),
            prizeRecord.name(),
            prizeRecord.description(),
            prizeRecord.templateText(),
            prizeRecord.voucherCode(),
            prizeRecord.validUntil(),
            winnerName,
            prizeRecord.isTemplate()
        );
    }

    /**
     * Create a new PrizeDialogFormRecord from a PrizeTemplateRecord
     */
    public static PrizeDialogFormRecord fromPrizeTemplateRecord(PrizeTemplateRecord template) {
        if (template == null) {
            return null;
        }

        return new PrizeDialogFormRecord(
            template.id(),
            template.name(),
            template.description(),
            template.templateText(),
            template.voucherCode(),
            template.validUntil(),
            null,
            true
        );
    }

    /**
     * Create a simple form record with minimal details
     */
    public static PrizeDialogFormRecord simple(Long id, String name) {
        return new PrizeDialogFormRecord(id, name, null, null, null, null, null, false);
    }

    /**
     * Create an empty form record for a prize
     */
    public static PrizeDialogFormRecord emptyPrize() {
        return new PrizeDialogFormRecord(null, "", "", "", null, null, null, false);
    }

    /**
     * Create an empty form record for a template
     */
    public static PrizeDialogFormRecord emptyTemplate() {
        return new PrizeDialogFormRecord(null, "", "", "", null, null, null, true);
    }

    /**
     * Convert to a PrizeRecord
     */
    public PrizeRecord toPrizeRecord(RaffleRecord raffle) {
        return new PrizeRecord(
            this.id,
            this.name,
            this.description,
            this.templateText,
            this.isTemplate,
            null, // No winner - winner is managed separately
            this.isTemplate ? null : raffle, // Templates don't have raffles
            this.voucherCode,
            this.validUntil
        );
    }

    /**
     * Convert to a PrizeTemplateRecord
     */
    public PrizeTemplateRecord toPrizeTemplateRecord() {
        if (!this.isTemplate) {
            throw new IllegalStateException("Cannot convert a prize to a template record");
        }
        
        return new PrizeTemplateRecord(
            this.id,
            this.name,
            this.description,
            this.templateText,
            this.voucherCode,
            this.validUntil
        );
    }

    /**
     * Create a new form with updated name
     */
    public PrizeDialogFormRecord withName(String name) {
        return new PrizeDialogFormRecord(this.id, name, this.description, this.templateText,
            this.voucherCode, this.validUntil, this.winnerName, this.isTemplate);
    }

    /**
     * Create a new form with updated description
     */
    public PrizeDialogFormRecord withDescription(String description) {
        return new PrizeDialogFormRecord(this.id, this.name, description, this.templateText,
            this.voucherCode, this.validUntil, this.winnerName, this.isTemplate);
    }

    /**
     * Create a new form with updated template text
     */
    public PrizeDialogFormRecord withTemplateText(String templateText) {
        return new PrizeDialogFormRecord(this.id, this.name, this.description, templateText,
            this.voucherCode, this.validUntil, this.winnerName, this.isTemplate);
    }

    /**
     * Create a new form with updated voucher code
     */
    public PrizeDialogFormRecord withVoucherCode(String voucherCode) {
        return new PrizeDialogFormRecord(this.id, this.name, this.description, this.templateText,
            voucherCode, this.validUntil, this.winnerName, this.isTemplate);
    }

    /**
     * Create a new form with updated valid until date
     */
    public PrizeDialogFormRecord withValidUntil(LocalDate validUntil) {
        return new PrizeDialogFormRecord(this.id, this.name, this.description, this.templateText,
            this.voucherCode, validUntil, this.winnerName, this.isTemplate);
    }

    /**
     * Create a new form with updated winner name
     */
    public PrizeDialogFormRecord withWinnerName(String winnerName) {
        return new PrizeDialogFormRecord(this.id, this.name, this.description, this.templateText,
            this.voucherCode, this.validUntil, winnerName, this.isTemplate);
    }
    
    /**
     * Create a new form with updated template flag
     */
    public PrizeDialogFormRecord withTemplateFlag(boolean isTemplate) {
        return new PrizeDialogFormRecord(this.id, this.name, this.description, this.templateText,
            this.voucherCode, this.validUntil, this.winnerName, isTemplate);
    }
}