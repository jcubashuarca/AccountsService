package com.nttdata.bootcamp.accountsservice.service;

import com.nttdata.bootcamp.accountsservice.dto.AccountReportResponse;
import com.nttdata.bootcamp.accountsservice.dto.AccountRequest;
import com.nttdata.bootcamp.accountsservice.dto.AccountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface AccountService {

    Flux<AccountResponse> getAll();

    Flux<AccountResponse> getAllByClient(String client);

    Mono<AccountReportResponse> getReport(String id, LocalDate from, LocalDate to);

    Mono<AccountResponse> getById(String id);

    Mono<Void> delete(String id);

    Mono<Void> deleteAll();

    Mono<AccountResponse> save(AccountRequest accountRequest);

    Mono<AccountResponse> update(String id,AccountRequest accountRequest);
}
