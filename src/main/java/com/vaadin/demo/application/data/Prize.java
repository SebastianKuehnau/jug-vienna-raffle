package com.vaadin.demo.application.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Entity
public class Prize extends AbstractEntity {

    private String name;

    private String winner ;

    @ManyToOne
    @JoinColumn(name = "raffle_id")
    @ToString.Exclude
    private Raffle raffle;

}
