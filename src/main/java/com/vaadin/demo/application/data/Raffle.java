package com.vaadin.demo.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Entity
public class Raffle extends AbstractEntity {

    private String meetup_event_id;

    @OneToMany(mappedBy = "raffle", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Prize> prizes = new HashSet<>();
}
