package com.vaadin.demo.application.repository;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PrizeRepository extends
        JpaRepository<Prize, Long>,
        JpaSpecificationExecutor<Prize> {

    List<Prize> findByRaffle(Raffle raffle);
    
    List<Prize> findByTemplateTrue();
    
    List<Prize> findByTemplateTrueAndNameContaining(String namePattern);
}
