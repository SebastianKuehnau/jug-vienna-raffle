package com.vaadin.demo.application.adapter.persistence.repository;

import com.vaadin.demo.application.adapter.persistence.data.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    /**
     * Find a member by their Meetup ID
     */
    Optional<Member> findByMeetupId(String meetupId);
}