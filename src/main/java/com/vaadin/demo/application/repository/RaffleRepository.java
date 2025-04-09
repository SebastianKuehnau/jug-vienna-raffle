package com.vaadin.demo.application.repository;

import com.vaadin.demo.application.data.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RaffleRepository
        extends JpaRepository<Raffle, Long>,
        JpaSpecificationExecutor<Raffle> {
}
