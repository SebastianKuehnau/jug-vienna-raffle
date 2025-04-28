package com.vaadin.demo.application.adapter.out.persistence.repository;

import java.util.Optional;

import com.vaadin.demo.application.adapter.out.persistence.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
}
