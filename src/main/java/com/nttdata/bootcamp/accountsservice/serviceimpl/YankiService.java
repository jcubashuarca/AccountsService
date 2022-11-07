package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.AccountYanki;
import com.nttdata.bootcamp.accountsservice.dto.LinkRequest;
import com.nttdata.bootcamp.accountsservice.dto.Result;
import com.nttdata.bootcamp.accountsservice.dto.Yanki;
import com.nttdata.bootcamp.accountsservice.exception.movement.MovementCreationException;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
public class YankiService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DebitCardRepository debitCardRepository;
    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private MessageService messageService;

    private final RMapReactive<Long, AccountYanki> accountMap;

    public YankiService(RedissonReactiveClient client) {
        this.accountMap = client.getMap("account", new TypedJsonJacksonCodec(Long.class, AccountYanki.class));
    }

    private Mono<Boolean> makeMovement(AccountYanki accountYanki, Double amount) {
        String type = amount > 0 ? "yanki in" : "yanki out";
        return debitCardRepository.findById(accountYanki.getDebitCard())
                .flatMap(debitCard -> accountRepository.findById(debitCard.getMainAccount()))
                .flatMap(accountLinked -> {
                    if (type.equals("yanki out") && accountLinked.getBalance() < -amount) {
                        return Mono.just(messageService.sendResult(Result.builder()
                                .to(accountYanki.getPhone())
                                .status("Failed")
                                .message("Not enough balance")
                                .build()));
                    }
                    accountLinked.setBalance(accountLinked.getBalance() + amount);
                    return movementRepository.save(Movement.builder()
                                    .type(type)
                                    .account(accountLinked.getId())
                                    .date(LocalDate.now())
                                    .amount(Math.abs(amount))
                                    .transactionFee(0.0)
                                    .build())
                            .then(accountRepository.save(accountLinked))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> receiveMovement(Yanki yanki) {
        return accountMap.get(yanki.getTo())
                .flatMap(accountTo -> {
                    if (!Objects.isNull(accountTo.getDebitCard())) {
                        return makeMovement(accountTo, yanki.getAmount())
                                .then(Mono.just(messageService.sendToYanki(yanki)))
                                .then(Mono.just(messageService.sendResult(Result.successToReceiver(yanki))));
                    }
                    return Mono.just(messageService.sendToYanki(yanki));
                });
    }

    private Mono<Boolean> sendMovement(AccountYanki accountFrom, Yanki yanki) {
        log.info("Yanki {}", yanki);
        return makeMovement(accountFrom, -yanki.getAmount())
                .flatMap(isSuccess -> isSuccess ?
                        Mono.just(messageService.sendResult(Result.successToSender(yanki))) :
                        Mono.just(false));
        //.then(Mono.just(messageService.sendResult(Result.successToSender(yanki))));
    }


    @Bean
    Consumer<Yanki> toaccount() {
        return yanki -> accountMap.get(yanki.getFrom())
                .flatMap(accountFrom -> {
                    if (!Objects.isNull(accountFrom.getDebitCard())) {
                        return sendMovement(accountFrom, yanki)
                                .flatMap(isSuccess -> isSuccess ?
                                        receiveMovement(yanki) :
                                        Mono.error(new MovementCreationException("Failed")));
                    }
                    return receiveMovement(yanki);
                })
                .doOnSuccess(x -> log.info("Account from: {}", x))
                .subscribe();
    }

    @Bean
    Consumer<LinkRequest> link() {
        return linkRequest -> {
            if (linkRequest.getState().equals("request")) {
                debitCardRepository.findById(linkRequest.getDebitCard())
                        .switchIfEmpty(Mono.error(new Throwable("Not found debit card")))
                        .flatMap(debitCard -> accountRepository.findById(debitCard.getMainAccount()))
                        .flatMap(account -> {
                            account.setBalance(account.getBalance() + linkRequest.getAmount());
                            linkRequest.setState("true");
                            return accountRepository.save(account)
                                    .then(Mono.just(messageService.sendToLink(linkRequest)));
                        })
                        .doOnSuccess(x -> log.info("Account from: {}", x))
                        .doOnError(x -> {
                            linkRequest.setState("false");
                            messageService.sendToLink(linkRequest);
                        })
                        .subscribe();
            }
        };
    }

}
