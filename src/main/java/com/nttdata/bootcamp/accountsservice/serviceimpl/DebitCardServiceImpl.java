package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.DebitCardRequest;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import com.nttdata.bootcamp.accountsservice.exception.debitcard.DebitCardCreationException;
import com.nttdata.bootcamp.accountsservice.exception.debitcard.DebitCardNotFoundException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.DebitCard;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.service.DebitCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DebitCardServiceImpl implements DebitCardService {

    private static final String NOT_FOUND_MESSAGE = "Debit Card not found with id: ";
    private static final String NOT_FOUND_MESSAGE_WITH_ID = "Debit Card not found with id: {}";

    private DebitCardRepository debitCardRepository;

    private AccountRepository accountRepository;

    private MovementRepository movementRepository;

    @Override
    public Flux<DebitCardResponse> getAll() {
        return debitCardRepository.findAll()
                .map(DebitCardResponse::fromModel);
    }

    @Override
    public Flux<DebitCardResponse> getAllByClient(String client) {
        return debitCardRepository.findCardByClient(client)
                .map(DebitCardResponse::fromModel)
                .doOnComplete(() -> log.info("Retrieving all Accounts"));
    }

    @Override
    public Mono<DebitCardResponse> getById(String id) {
        return debitCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_ID, id, ex))
                .map(DebitCardResponse::fromModel);
    }

    @Override
    public Mono<Void> delete(String id) {
        return debitCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_ID, id, ex))
                .flatMap(existingCard ->
                        debitCardRepository.delete(existingCard)
                )
                .doOnSuccess(ex -> log.info("Delete debit card with id: {}", id));
    }

    @Override
    public Mono<Void> deleteAll() {
        return debitCardRepository.deleteAll()
                .doOnSuccess(ex -> log.info("Delete all debit cards"));
    }

    public Flux<DebitCard> validateAndFindCard(DebitCardRequest debitCardRequest) {
        return accountRepository.findById(debitCardRequest.getAccount())
                .switchIfEmpty(Mono.error(new DebitCardCreationException("Account does not exist")))
                .flatMapMany(account -> {
                    if (!account.getClient().equals(debitCardRequest.getClient())) {
                        return Mono.error(new DebitCardCreationException("Account is not associated with the client"));
                    }
                    return debitCardRepository.findCardByNumber(debitCardRequest.getNumber());
                });
    }

    @Override
    public Mono<DebitCardResponse> link(DebitCardRequest debitCardRequest) {
        return validateAndFindCard(debitCardRequest)
                .switchIfEmpty(Mono.error(new DebitCardCreationException("Card is not created yet")))
                .next()
                .flatMap(card -> {
                    card.getOptionalAccounts().add(debitCardRequest.getAccount());
                    return debitCardRepository.save(card);
                })
                .map(DebitCardResponse::fromModel);
    }

    @Override
    public Mono<DebitCardResponse> save(DebitCardRequest debitCardRequest) {
        return validateAndFindCard(debitCardRequest)
                .hasElements()
                .flatMap(cardExist -> {
                    if (Boolean.TRUE.equals(cardExist)) {
                        return Mono.error(new DebitCardCreationException("Card number is already in use"));
                    }
                    return debitCardRepository.save(debitCardRequest.toModel());
                })
                .map(DebitCardResponse::fromModel);
    }

    public Mono<Boolean> hasEnoughMoney(MovementRequest movementRequest) {
        return debitCardRepository.findById(movementRequest.getAccount())
                .flatMapMany(debitCard -> Flux.fromIterable(debitCard.getOptionalAccounts()))
                .flatMap(accountId -> accountRepository.findById(accountId))
                .map(Account::getBalance)
                .reduce(0.0, Double::sum)
                .map(sum -> sum >= movementRequest.getAmount());
    }

    @Override
    public Mono<List<MovementResponse>> makeMovement(MovementRequest movementRequest) {
        return hasEnoughMoney(movementRequest)
                .flatMap(hasEnoughMoney -> {
                    if (Boolean.FALSE.equals(hasEnoughMoney)) {
                        return Mono.error(new DebitCardCreationException("Not enough balance in linked accounts"));
                    }
                    return debitCardRepository.findById(movementRequest.getAccount());
                })
                .flatMapMany(debitCard -> Flux.fromIterable(debitCard.getOptionalAccounts()))
                .flatMap(accountId -> accountRepository.findById(accountId))
                .takeWhile(x -> movementRequest.getAmount() > 0)
                .flatMap(account -> {
                    movementRequest.setAccount(account.getId());

                    double originalAmount = movementRequest.getAmount();
                    double lastBalance = account.getBalance();
                    double updatedAmount = originalAmount - lastBalance;

                    movementRequest.setAmount(updatedAmount);

                    Movement newMovement = updatedAmount > 0.0 ? movementRequest.toModelWithAmount(lastBalance) :
                            movementRequest.toModelWithAmount(originalAmount);

                    account.makeMovement(newMovement);

                    return accountRepository.save(account)
                            .then(movementRepository.save(newMovement));

                })
                .map(MovementResponse::fromModel)
                .collectList();
    }

    @Override
    public Flux<MovementResponse> getLastMovements(String id) {
        return debitCardRepository.findById(id)
                .flatMapMany(debitCard -> Flux.fromIterable(debitCard.getOptionalAccounts()))
                .flatMap(accountId -> movementRepository.findByAccount(accountId))
                .filter(movement -> movement.getType().equalsIgnoreCase("pay") ||
                        movement.getType().equalsIgnoreCase("withdraw debit"))
                .sort(Comparator.comparing(Movement::getDate))
                .take(10)
                .map(MovementResponse::fromModel);
    }

    @Override
    public Mono<Double> getBalanceOfMainAccount(String id) {
        return debitCardRepository.findById(id)
                .flatMap(debitCard -> accountRepository.findById(debitCard.getMainAccount()))
                .map(Account::getBalance);
    }


    @Override
    public Mono<DebitCardResponse> update(String id, DebitCardRequest debitCardRequest) {
        return debitCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_ID, id, ex))
                .flatMap(existingCard -> {
                    existingCard.setNumber(debitCardRequest.getNumber());
                    return debitCardRepository.save(existingCard);
                })
                .map(DebitCardResponse::fromModel)
                .doOnSuccess(res -> log.info("Updated Debit Card with ID: {}", res.getId()));
    }
}
