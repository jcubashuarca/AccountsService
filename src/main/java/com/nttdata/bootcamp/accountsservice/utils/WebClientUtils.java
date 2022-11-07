package com.nttdata.bootcamp.accountsservice.utils;

import com.nttdata.bootcamp.accountsservice.exception.account.AccountCreationException;
import com.nttdata.bootcamp.accountsservice.model.Client;
import com.nttdata.bootcamp.accountsservice.model.CreditCard;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class WebClientUtils {
    private final WebClient webClient;
    private static final String NOT_FOUND_MESSAGE = "Not found Client with ID: ";
    @Value("${services-uri.clients}")
    private String clientsService;

    @Value("${services-uri.credits}")
    private String creditsService;

    public WebClientUtils() {
        this.webClient = WebClient.create("http://localhost:8080/clients");
    }

    public WebClientUtils(WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "clients",fallbackMethod = "clientsUnavailable")
    public Mono<Client> getClient(String id) {
        return webClient
                .mutate()
                .baseUrl(clientsService)
                .build()
                .get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new AccountCreationException(NOT_FOUND_MESSAGE + id)))
                .bodyToMono(Client.class);
    }

    public Mono<String> clientsUnavailable(String id, Exception ex) {
        return Mono.error(new Exception("Client service unavailable"));
    }

    public Flux<CreditCard> getCredits(String id) {
        return webClient
                .mutate()
                .baseUrl(creditsService+"/credit_cards/client")
                .build()
                .get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new AccountCreationException(NOT_FOUND_MESSAGE + id)))
                .bodyToFlux(CreditCard.class);
    }

    public Mono<Boolean> isClientWithCreditDebt(String id) {
        return webClient
                .mutate()
                .baseUrl(creditsService+"/credit_cards/client")
                .build()
                .get()
                .uri("/{id}/is_debt", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new AccountCreationException(NOT_FOUND_MESSAGE + id)))
                .bodyToMono(Boolean.class);
    }

}
