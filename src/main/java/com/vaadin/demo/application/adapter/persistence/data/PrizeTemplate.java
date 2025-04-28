package com.vaadin.demo.application.adapter.persistence.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Template for prizes that can be used to create actual prizes
 */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class PrizeTemplate extends AbstractEntity {

    @ToString.Include
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "template_text", length = 4000)
    private String templateText;

    /**
     * Optional voucher code for this prize template
     */
    private String voucherCode;

    /**
     * Optional expiration date until when the prize should be redeemed
     */
    private LocalDate validUntil;

    /**
     * Create a new Prize instance from this template
     */
    public Prize createPrize() {
        Prize prize = new Prize();
        prize.setName(this.getName());
        prize.setDescription(this.getDescription());
        prize.setTemplateText(this.getTemplateText());
        prize.setVoucherCode(this.getVoucherCode());
        prize.setValidUntil(this.getValidUntil());

        return prize;
    }
}