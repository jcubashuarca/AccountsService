package com.nttdata.bootcamp.accountsservice.service;

import com.nttdata.bootcamp.accountsservice.dto.DebitCardRequest;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DebitCardService {

    Flux<DebitCardResponse> getAll();

    Flux<DebitCardResponse> getAllByClient(String client);

    Mono<DebitCardResponse> getById(String id);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

    Mono<DebitCardResponse> save(DebitCardRequest debitCardRequest);

    Mono<List<MovementResponse>> makeMovement(MovementRequest movementRequest);

    Flux<MovementResponse> getLastMovements(String id);

    Mono<Double> getBalanceOfMainAccount(String id);

    Mono<DebitCardResponse> link(DebitCardRequest debitCardRequest);

    Mono<DebitCardResponse> update(String id,DebitCardRequest debitCardRequest);
}
