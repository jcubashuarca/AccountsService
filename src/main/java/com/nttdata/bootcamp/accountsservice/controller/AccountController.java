package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.dto.AccountReportResponse;
import com.nttdata.bootcamp.accountsservice.dto.AccountRequest;
import com.nttdata.bootcamp.accountsservice.dto.AccountResponse;
import com.nttdata.bootcamp.accountsservice.dto.FeeResponse;
import com.nttdata.bootcamp.accountsservice.service.AccountService;
import com.nttdata.bootcamp.accountsservice.service.MovementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {
    private AccountService service;

    private MovementService movementService;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return service.getAll();
    }

    @GetMapping("/client/{client}")
    public Flux<AccountResponse> getAllAccountsByClient(@PathVariable final String client) {
        return service.getAllByClient(client);
    }

    @GetMapping("{id}")
    public Mono<AccountResponse> getAccount(@PathVariable final String id) {
        return service.getById(id);
    }

    @GetMapping("{id}/dailyBalance")
    public Mono<Double> getReportOfDailyBalance(@PathVariable final String id) {
        return movementService.getReportOfDailyBalance(id);
    }

    @GetMapping("{id}/feeReport/from/{yearFrom}/{monthFrom}/{dayFrom}/to/{yearTo}/{monthTo}/{dayTo}")
    public Flux<FeeResponse> getAllFeesByAccountAndPeriod(@PathVariable final String id,
                                                          @PathVariable final Integer yearFrom,
                                                          @PathVariable final Integer monthFrom,
                                                          @PathVariable final Integer dayFrom,
                                                          @PathVariable final Integer yearTo,
                                                          @PathVariable final Integer monthTo,
                                                          @PathVariable final Integer dayTo) {

        LocalDate from = LocalDate.of(yearFrom, monthFrom, dayFrom);
        LocalDate to = LocalDate.of(yearTo, monthTo, dayTo);
        return movementService.getAllFeesByAccountAndPeriod(id, from, to);
    }

    @GetMapping("{id}/report/from/{yearFrom}/{monthFrom}/{dayFrom}/to/{yearTo}/{monthTo}/{dayTo}")
    public Mono<AccountReportResponse> getReportByAccountAndPeriod(@PathVariable final String id,
                                                                   @PathVariable final Integer yearFrom,
                                                                   @PathVariable final Integer monthFrom,
                                                                   @PathVariable final Integer dayFrom,
                                                                   @PathVariable final Integer yearTo,
                                                                   @PathVariable final Integer monthTo,
                                                                   @PathVariable final Integer dayTo) {

        LocalDate from = LocalDate.of(yearFrom, monthFrom, dayFrom);
        LocalDate to = LocalDate.of(yearTo, monthTo, dayTo);
        return service.getReport(id, from, to);
    }

    @GetMapping("/client/{client}/dailyBalance")
    public Flux<Map<String, Double>> getAllAccountsReportByClient(@PathVariable final String client) {
        return movementService.getAllReportsByClient(client);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponse> saveAccount(@RequestBody final AccountRequest accountRequest) {
        return service.save(accountRequest);
    }

    @PutMapping("{id}")
    public Mono<AccountResponse> updateAccount(@PathVariable final String id,
                                               @RequestBody final AccountRequest accountRequest) {
        return service.update(id, accountRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteAccount(@PathVariable final String id) {
        return service.delete(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllAccounts() {
        return service.deleteAll();
    }
}
