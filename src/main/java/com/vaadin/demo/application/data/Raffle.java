package com.vaadin.demo.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Raffle extends AbstractEntity {

    private String meetup_event_id;

    @OneToMany(mappedBy = "raffle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Prize> prizes = new HashSet<>();


}
