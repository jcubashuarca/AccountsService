package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.FeeResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import com.nttdata.bootcamp.accountsservice.exception.movement.MovementCreationException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.service.MovementService;
import com.nttdata.bootcamp.accountsservice.utils.MovementUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class MovementServiceImpl implements MovementService {

    private static final String NOT_FOUND_MESSAGE = "Movement not found with id: ";

    private MovementRepository movementRepository;

    private AccountRepository accountRepository;

    private MovementUtils movementUtils;

    @Override
    public Flux<MovementResponse> getAll() {
        return movementRepository.findAll()
                .map(MovementResponse::fromModel);
    }

    @Override
    public Mono<MovementResponse> getById(String id) {
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException(NOT_FOUND_MESSAGE + id)))
                .map(MovementResponse::fromModel);
    }

    @Override
    public Flux<MovementResponse> getAllMovementsByAccount(String account) {
        return movementRepository.findByAccount(account)
                .map(MovementResponse::fromModel);
    }

    @Override
    public Mono<Void> delete(String id) {
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException(NOT_FOUND_MESSAGE + id)))
                .flatMap(existingAccount ->
                        movementRepository.delete(existingAccount)
                );
    }

    @Override
    public Mono<Void> deleteAll() {
        return movementRepository.deleteAll();
    }

    private Flux<Movement> getMovementsOfCurrentMonthByAccount(String account) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();
        return movementRepository.findByAccountAndDateBetween(account, firstOfMonth, last);
    }

    private Mono<Long> getCountOfMovementsOfCurrentMonthByAccount(String account) {
        return getMovementsOfCurrentMonthByAccount(account)
                .count();
    }

    @Override
    public Mono<MovementResponse> save(MovementRequest movementRequest) {
        return Mono.just(movementRequest)
                .map(MovementRequest::toModel)
                .flatMap(movement -> accountRepository.findById(movement.getAccount())
                        .switchIfEmpty(Mono.error(new MovementCreationException("Account not found with id: "
                                + movement.getAccount())))
                        .zipWith(getCountOfMovementsOfCurrentMonthByAccount(movement.getAccount()))
                        .flatMap(result -> {
                            Account existingAccount = result.getT1();
                            Long movementsCurrentMonth = result.getT2();

                            if (!existingAccount.canFixedAccountMove())
                                return Mono.error(
                                        new MovementCreationException("Fixed account can only make movement on: " +
                                                existingAccount.getMovementDay() + " of each month"));

                            if (!existingAccount.isMovementInAccountLimit(movementsCurrentMonth))
                                movementUtils.setTransactionFee(movement, existingAccount.getType());

                            if (!existingAccount.isMovementValid(movement))
                                return Mono.error(new MovementCreationException("Not enough money"));

                            return accountRepository.save(existingAccount)
                                    .then(movementRepository.save(movement));
                        }))
                .map(MovementResponse::fromModel)
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()));
    }

    @Override
    public Mono<MovementResponse> update(String id, MovementRequest movementRequest) {

        Mono<Movement> movementMono = movementRepository.findById(id)
                .switchIfEmpty(Mono.error(new MovementCreationException(NOT_FOUND_MESSAGE + movementRequest.getAccount())))
                .flatMap(existingMovement -> {
                    Movement differenceMovement =
                            movementUtils.createDifferenceMovement(existingMovement, movementRequest);
                    return Mono.just(differenceMovement);
                });

        Mono<Account> accountMono = accountRepository.findById(movementRequest.getAccount())
                .switchIfEmpty(Mono.error(new MovementCreationException("Account not found with id: " + movementRequest.getAccount())));

        return movementMono.zipWith(accountMono)
                .flatMap(result -> {
                    Movement differenceMovement = result.getT1();
                    log.info("differenceMovement: {}",differenceMovement);
                    Account accountFound = result.getT2();
                    if (accountFound.isMovementValid(differenceMovement)) {
                        accountFound.makeMovement(differenceMovement);
                        return accountRepository.save(accountFound);
                    } else {
                        return Mono.error(new MovementCreationException("Not enough money"));
                    }
                })
                .then(Mono.just(movementRequest))
                .map(MovementRequest::toModel)
                .map(movement -> {
                    movement.setId(id);
                    return movement;
                })
                .flatMap(movement -> movementRepository.save(movement))
                .map(MovementResponse::fromModel)
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()));

    }

    @Override
    public Mono<Double> getReportOfDailyBalance(String account) {

        int numDays = LocalDate.now().getDayOfMonth();

        Mono<Double> lastBalance = accountRepository.findById(account)
                .map(Account::getBalance); //145

        Mono<Double> sumOfMonthMovements = getMovementsOfCurrentMonthByAccount(account)
                .reduce(0.0, (x1, x2) -> x1 + x2.getAmountSigned());

        Mono<Double> sumOfAverageDailyMovements = getMovementsOfCurrentMonthByAccount(account)
                .map(movement -> movement.getAmountSigned() * (numDays - movement.getDayOfMovement() + 1))
                .reduce(0.0, Double::sum);

        return Mono.zip(lastBalance, sumOfMonthMovements, sumOfAverageDailyMovements)
                .map(result -> {
                    Double lastBalanceResult = result.getT1();
                    Double sumOfMonthMovementsResult = result.getT2();
                    Double sumOfAverageDailyMovementsResult = result.getT3();
                    Double initialBalance = (lastBalanceResult - sumOfMonthMovementsResult) * numDays;
                    return (initialBalance + sumOfAverageDailyMovementsResult) / numDays;
                });
    }

    @Override
    public Flux<Map<String, Double>> getAllReportsByClient(String client) {
        return accountRepository.findAccountByClient(client)
                .flatMap(account -> getReportOfDailyBalance(account.getId())
                        .map(result -> Collections.singletonMap(account.getId(), result)));
    }

    @Override
    public Flux<FeeResponse> getAllFeesByAccountAndPeriod(String account, LocalDate from, LocalDate to) {
        return movementRepository.findByAccountAndDateBetween(account, from, to)
                .filter(movement -> movement.getTransactionFee() > 0.0)
                .map(movement -> new FeeResponse(movement.getDate(), movement.getTransactionFee()));
    }


}
