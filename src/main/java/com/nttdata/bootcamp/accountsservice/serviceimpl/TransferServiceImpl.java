package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.TransferRequest;
import com.nttdata.bootcamp.accountsservice.dto.TransferResponse;
import com.nttdata.bootcamp.accountsservice.exception.transfer.TransferCreationException;
import com.nttdata.bootcamp.accountsservice.exception.transfer.TransferNotFoundException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.model.Transfer;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import com.nttdata.bootcamp.accountsservice.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TransferServiceImpl implements TransferService {

    private static final String NOT_FOUND_MESSAGE = "Movement not found with id: ";

    private TransferRepository transferRepository;

    private AccountRepository accountRepository;

    private MovementRepository movementRepository;


    @Override
    public Flux<TransferResponse> getAll() {
        return transferRepository.findAll()
                .map(TransferResponse::fromModel);
    }

    @Override
    public Flux<TransferResponse> getAllByAccount(String account) {
        return transferRepository.findByFromOrTo(account, account)
                .map(TransferResponse::fromModel);
    }

    @Override
    public Mono<TransferResponse> getById(String id) {
        return transferRepository.findById(id)
                .switchIfEmpty(Mono.error(new TransferNotFoundException(NOT_FOUND_MESSAGE + id)))
                .map(TransferResponse::fromModel);
    }


    @Override
    public Mono<Void> delete(String id) {
        return transferRepository.findById(id)
                .switchIfEmpty(Mono.error(new TransferNotFoundException(NOT_FOUND_MESSAGE + id)))
                .flatMap(existingAccount ->
                        transferRepository.delete(existingAccount)
                );
    }

    @Override
    public Mono<Void> deleteAll() {
        return transferRepository.deleteAll();
    }


    @Override
    public Mono<TransferResponse> save(TransferRequest transferRequest) {

        Mono<Account> accountFrom = accountRepository.findById(transferRequest.getFrom());
        Mono<Account> accountTo = accountRepository.findById(transferRequest.getTo());

        return accountFrom
                .zipWith(accountTo)
                .flatMap(accounts -> {
                    Account from = accounts.getT1();
                    Account to = accounts.getT2();

                    if (from.getBalance() < transferRequest.getAmount())
                        return Mono.error(new TransferCreationException("Not enough money to make this transfer"));

                    if (from.getClient().equals(to.getClient()))
                        return transferSameClient(transferRequest, from, to);

                    return transferSameClient(transferRequest, from, to);
                }).map(TransferResponse::fromModel);
    }

    public Mono<Transfer> transferSameClient(TransferRequest transferRequest, Account from, Account to) {

        Mono<Movement> newMovementOut = movementRepository.save(new Movement(null,
                "Transfer Out",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                from.getId()));

        Mono<Movement> newMovementIn = movementRepository.save(new Movement(null,
                "Transfer In",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                to.getId()));

        from.setBalance(from.getBalance() - transferRequest.getAmount());
        to.setBalance(to.getBalance() + transferRequest.getAmount());

        Mono<Account> updatedFromAccount = accountRepository.save(from);
        Mono<Account> updatedToAccount = accountRepository.save(to);

        return Mono.zip(newMovementOut, newMovementIn, updatedFromAccount, updatedToAccount)
                .then(Mono.just(transferRequest))
                .map(TransferRequest::toModel)
                .flatMap(transfer -> transferRepository.save(transfer));
    }

    @Override
    public Mono<TransferResponse> update(String id, TransferRequest transferRequest) {

        return Mono.just(transferRequest)
                .map(TransferRequest::toModel)
                .flatMap(transfer -> transferRepository.save(transfer))
                .map(TransferResponse::fromModel)
                .onErrorMap(ex -> new TransferNotFoundException(ex.getMessage()));

    }

}
