package com.vaadin.demo.application.adapter.out.persistence.repository;

import com.vaadin.demo.application.adapter.out.persistence.data.Prize;
import com.vaadin.demo.application.adapter.out.persistence.data.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrizeRepository extends
        JpaRepository<Prize, Long>,
        JpaSpecificationExecutor<Prize> {

    List<Prize> findByRaffle(Raffle raffle);

}
