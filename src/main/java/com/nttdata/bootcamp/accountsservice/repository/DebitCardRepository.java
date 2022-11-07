package com.nttdata.bootcamp.accountsservice.repository;

import com.nttdata.bootcamp.accountsservice.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard,String> {
    Flux<DebitCard> findCardByClient(String client);
    Flux<DebitCard> findCardByNumber(String number);

    Flux<DebitCard> findCardByMainAccount(String number);
}
