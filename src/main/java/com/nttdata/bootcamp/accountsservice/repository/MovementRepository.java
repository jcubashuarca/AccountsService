package com.nttdata.bootcamp.accountsservice.repository;

import com.nttdata.bootcamp.accountsservice.model.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface MovementRepository extends ReactiveMongoRepository<Movement,String> {
    Flux<Movement> findByAccountAndDateBetween(String account, LocalDate from, LocalDate to);
    Flux<Movement> findByAccount(String account);

}
