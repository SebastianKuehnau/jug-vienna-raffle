package com.vaadin.demo.application.services;

import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public Optional<Member> get(Long id) {
        return repository.findById(id);
    }
    
    public Optional<Member> getByMeetupId(String meetupId) {
        return repository.findByMeetupId(meetupId);
    }

    public Member save(Member entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Member> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Member> list(Pageable pageable, Specification<Member> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}