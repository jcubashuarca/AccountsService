package com.nttdata.bootcamp.accountsservice.controller;


import com.nttdata.bootcamp.accountsservice.dto.DebitCardRequest;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import com.nttdata.bootcamp.accountsservice.service.DebitCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/accounts/debitCard")
@AllArgsConstructor
@Slf4j
public class DebitCardController {
    private DebitCardService service;

    @GetMapping
    public Flux<DebitCardResponse> getAllDebitCards(){
        return service.getAll();
    }

    @GetMapping("{id}")
    public Mono<DebitCardResponse> getDebitCard(@PathVariable String id){
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DebitCardResponse> saveDebitCard(@RequestBody DebitCardRequest debitCardRequest){
        return service.save(debitCardRequest);
    }

    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DebitCardResponse> linkDebitCard(@RequestBody DebitCardRequest debitCardRequest){
        return service.link(debitCardRequest);
    }

    @PostMapping("/movement")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<List<MovementResponse>> movementDebitCard(@RequestBody MovementRequest movementRequest){
        return service.makeMovement(movementRequest);
    }

    @GetMapping("{id}/movements")
    public Flux<MovementResponse> getLastMovements(@PathVariable String id){
        return service.getLastMovements(id);
    }

    @GetMapping("{id}/balance")
    public Mono<Double> getBalanceOfMainAccount(@PathVariable String id){
        return service.getBalanceOfMainAccount(id);
    }

    @PutMapping("{id}")
    public Mono<DebitCardResponse> updateDebitCard(@PathVariable String id,
                                                        @RequestBody DebitCardRequest debitCardRequest){
        return service.update(id, debitCardRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteDebitCard(@PathVariable String id){
        return service.delete(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllDebitCards(){
        return service.deleteAll();
    }
}
