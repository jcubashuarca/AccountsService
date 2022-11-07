package com.nttdata.bootcamp.accountsservice.service;

import com.nttdata.bootcamp.accountsservice.dto.TransferRequest;
import com.nttdata.bootcamp.accountsservice.dto.TransferResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferService {

    Flux<TransferResponse> getAll();

    Flux<TransferResponse> getAllByAccount(String account);

    Mono<TransferResponse> getById(String id);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

    Mono<TransferResponse> save(TransferRequest transferRequest);

    Mono<TransferResponse> update(String id,TransferRequest transferRequest);
}
