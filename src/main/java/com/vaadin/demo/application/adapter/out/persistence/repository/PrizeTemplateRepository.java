package com.vaadin.demo.application.adapter.out.persistence.repository;

import com.vaadin.demo.application.adapter.out.persistence.data.PrizeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeTemplateRepository extends JpaRepository<PrizeTemplate, Long> {

    /**
     * Find prize templates by name (case-insensitive, partial match)
     */
    List<PrizeTemplate> findByNameContainingIgnoreCase(String name);
}