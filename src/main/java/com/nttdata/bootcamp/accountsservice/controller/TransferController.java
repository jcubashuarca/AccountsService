package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.dto.TransferRequest;
import com.nttdata.bootcamp.accountsservice.dto.TransferResponse;
import com.nttdata.bootcamp.accountsservice.service.TransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts/transfer")
@AllArgsConstructor
@Slf4j
public class TransferController {
    private TransferService service;

    @GetMapping
    public Flux<TransferResponse> getAllTransfers(){
        return service.getAll();
    }

    @GetMapping("/product/{account}")
    public Flux<TransferResponse> getAllTransfersByAccount(@PathVariable String account){
        return service.getAllByAccount(account);
    }

    @GetMapping("{id}")
    public Mono<TransferResponse> getTransfer(@PathVariable String id){
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransferResponse> saveTransfer(@RequestBody TransferRequest movementRequest){
        return service.save(movementRequest);
    }

    @PutMapping("{id}")
    public Mono<TransferResponse> updateTransfer(@PathVariable String id,
                                                        @RequestBody TransferRequest movementRequest){
        return service.update(id, movementRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteTransfer(@PathVariable String id){
        return service.delete(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllTransfers(){
        return service.deleteAll();
    }
}
