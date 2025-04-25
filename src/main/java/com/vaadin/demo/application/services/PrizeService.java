package com.vaadin.demo.application.services;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.repository.PrizeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrizeService {

    private final PrizeRepository repository;

    public PrizeService(PrizeRepository prizeRepository) {
        this.repository = prizeRepository;
    }

    public Optional<Prize> get(Long id) {
        return repository.findById(id);
    }

    public List<Prize> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream().toList();
    }

    public Prize save(Prize prizeBean) {
        return this.repository.save(prizeBean);
    }

    public void delete(Prize prize) {
        this.repository.delete(prize);
    }

    public List<Prize> findByRaffle(Raffle raffle) {
        return this.repository.findByRaffle(raffle);
    }
}
