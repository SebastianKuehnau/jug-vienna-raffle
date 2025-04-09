package com.vaadin.demo.application.repository;

import com.vaadin.demo.application.data.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrizeRepository extends
        JpaRepository<Prize, Long>,
        JpaSpecificationExecutor<Prize> {
}
