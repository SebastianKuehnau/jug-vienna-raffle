package com.vaadin.demo.application.services;

import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.repository.RaffleRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RaffleService {

    private final RaffleRepository repository;

    public RaffleService(RaffleRepository repository) {
        this.repository = repository;
    }

    public Optional<Raffle> get(Long id) {
        return repository.findById(id);
    }

    public Raffle save(Raffle entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Raffle> list(Pageable pageable) {
        return repository.findAll(pageable).stream().toList();
    }

    public List<Raffle> list(Pageable pageable, Specification<Raffle> filter) {
        return repository.findAll(filter, pageable).stream().toList();
    }

    public long count() {
        return repository.count();
    }

    public long count(Specification<Raffle> filter) {
        return repository.count(filter);
    }
}
