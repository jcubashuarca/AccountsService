package com.nttdata.bootcamp.accountsservice.repository;

import com.nttdata.bootcamp.accountsservice.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account,String> {

    Mono<Account> findByNumber(String number);
    Flux<Account> findAccountByClientAndType(String client, String type);
    Flux<Account> findAccountByClient(String client);

}
