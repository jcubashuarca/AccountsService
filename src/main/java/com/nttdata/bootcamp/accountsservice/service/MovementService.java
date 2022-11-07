package com.nttdata.bootcamp.accountsservice.service;

import com.nttdata.bootcamp.accountsservice.dto.FeeResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

public interface MovementService {

    Flux<MovementResponse> getAll();

    Mono<MovementResponse> getById(String id);

    Flux<MovementResponse> getAllMovementsByAccount(String account);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

    Mono<MovementResponse> save(MovementRequest movementRequest);

    Mono<MovementResponse> update(String id,MovementRequest movementRequest);

    Mono<Double> getReportOfDailyBalance(String account);

    Flux<Map<String,Double>> getAllReportsByClient(String client);

    Flux<FeeResponse> getAllFeesByAccountAndPeriod(String account, LocalDate from, LocalDate to);

}
