package com.vaadin.demo.application.domain.model;

import java.time.LocalDate;

/**
 * Immutable domain object for Prize form data Used in the UI layer to prevent direct dependency on
 * JPA entities
 */
public record PrizeFormRecord(
    Long id,
    String name,
    String description,
    String templateText,
    String voucherCode,
    LocalDate validUntil,
    String winnerName
) {

  /**
   * Create a new PrizeFormRecord from a PrizeRecord
   */
  public static PrizeFormRecord fromPrizeRecord(PrizeRecord prizeRecord) {
      if (prizeRecord == null) {
          return null;
      }

    String winnerName = null;
    if (prizeRecord.winner() != null && prizeRecord.winner().member() != null) {
      winnerName = prizeRecord.winner().member().name();
    }

    return new PrizeFormRecord(
        prizeRecord.id(),
        prizeRecord.name(),
        prizeRecord.description(),
        prizeRecord.templateText(),
        prizeRecord.voucherCode(),
        prizeRecord.validUntil(),
        winnerName
    );
  }

  /**
   * Create a simple form record with minimal details
   */
  public static PrizeFormRecord simple(Long id, String name) {
    return new PrizeFormRecord(id, name, null, null, null, null, null);
  }

  /**
   * Create an empty form record
   */
  public static PrizeFormRecord empty() {
    return new PrizeFormRecord(null, "", "", null, null, null, null);
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
        null, // No winner - winner is managed separately
        raffle,
        this.voucherCode,
        this.validUntil
    );
  }

  /**
   * Create a new form with updated name
   */
  public PrizeFormRecord withName(String name) {
    return new PrizeFormRecord(this.id, name, this.description, this.templateText,
        this.voucherCode, this.validUntil, this.winnerName);
  }

  /**
   * Create a new form with updated description
   */
  public PrizeFormRecord withDescription(String description) {
    return new PrizeFormRecord(this.id, this.name, description, this.templateText,
        this.voucherCode, this.validUntil, this.winnerName);
  }

  /**
   * Create a new form with updated template text
   */
  public PrizeFormRecord withTemplateText(String templateText) {
    return new PrizeFormRecord(this.id, this.name, this.description, templateText,
        this.voucherCode, this.validUntil, this.winnerName);
  }

  /**
   * Create a new form with updated voucher code
   */
  public PrizeFormRecord withVoucherCode(String voucherCode) {
    return new PrizeFormRecord(this.id, this.name, this.description, this.templateText,
        voucherCode, this.validUntil, this.winnerName);
  }

  /**
   * Create a new form with updated valid until date
   */
  public PrizeFormRecord withValidUntil(LocalDate validUntil) {
    return new PrizeFormRecord(this.id, this.name, this.description, this.templateText,
        this.voucherCode, validUntil, this.winnerName);
  }

  public PrizeFormRecord withWinnerName(String winnerName) {
    return new PrizeFormRecord(this.id, this.name, this.description, this.templateText,
        this.voucherCode, this.validUntil, winnerName);
  }
}