package com.vaadin.demo.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;


@Data
@Entity
public class Prize extends AbstractEntity {

    private String name;

    private String winner ;

    @ManyToOne
    @JoinColumn(name = "raffle_id")
    private Raffle raffle;


}
