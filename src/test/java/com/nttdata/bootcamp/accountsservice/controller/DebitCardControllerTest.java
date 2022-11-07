package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.DebitCard;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import com.nttdata.bootcamp.accountsservice.utils.AccountUtils;
import com.nttdata.bootcamp.accountsservice.utils.WebClientUtils;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardRequest;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DebitCardControllerTest {

    private static final String CARD_ID = "627760015d3f4d6ace96c44b";
    private static final String CARD_NUMBER = "44452-454548-8787841";
    private static final String CARD_CLIENT = "627718aff4256e7261ae367f";
    private static final String CARD_OTHER_CLIENT = "627718aff4256e7261ae367g";
    private static final String CARD_MAIN_ACCOUNT = "627760015d3f4d6ace96c44b";
    private static final String CARD_OTHER_ACCOUNT = "627760015d3f4d6ace96c44c";

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private DebitCardRepository debitCardRepository;
    @MockBean
    private MovementRepository movementRepository;
    @MockBean
    private TransferRepository transferRepository;

    @MockBean
    private AccountUtils accountUtils;
    @MockBean
    private WebClientUtils webClientUtils;

    @Autowired
    WebTestClient client;

    @Test
    void getAllDebitCards() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findAll())
                .thenReturn(Flux.just(debitCard));

        List<DebitCardResponse> debitCardResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/debitCard").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DebitCardResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getDebitCard() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        DebitCardResponse debitCardResponse = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/debitCard/{id}").build(CARD_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(DebitCardResponse.class)
                .returnResult().getResponseBody();
    }

    @Test
    void saveDebitCard() {
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_MAIN_ACCOUNT)
                .build();

        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>(Arrays.asList(CARD_MAIN_ACCOUNT)))
                .build();
        //WHEN
        Account account = new Account();
        account.setClient(CARD_CLIENT);

        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByNumber(any()))
                .thenReturn(Flux.empty());

        when(debitCardRepository.save(any()))
                .thenReturn(Mono.just(debitCard));

        DebitCardResponse debitCardResponse = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard")
                        .build())
                .bodyValue(debitCardRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DebitCardResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void linkDebitCard() {
        //GIVEN
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_OTHER_ACCOUNT)
                .build();

        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>(Arrays.asList(CARD_MAIN_ACCOUNT)))
                .build();

        DebitCard debitCardUpdated = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>(Arrays.asList(CARD_MAIN_ACCOUNT, CARD_OTHER_ACCOUNT)))
                .build();

        //WHEN
        Account account = new Account();
        account.setClient(CARD_CLIENT);

        when(accountRepository.findById(CARD_OTHER_ACCOUNT))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByNumber(any()))
                .thenReturn(Flux.just(debitCard));

        when(debitCardRepository.save(any()))
                .thenReturn(Mono.just(debitCardUpdated));

        DebitCardResponse debitCardResponse = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard/link")
                        .build())
                .bodyValue(debitCardRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DebitCardResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void movementDebitCard() {
        MovementRequest movementRequest = MovementRequest.builder()
                .account("627760015d3f4d6ace96c40a")
                .type("pay")
                .amount(100.0)
                .build();

        DebitCard debitCard = DebitCard.builder()
                .id("627760015d3f4d6ace96c44a")
                .number("4445-54545-4545454")
                .client("627760015d3f4d6ace96c55c")
                .mainAccount("627760015d3f4d6ace96c44b")
                .optionalAccounts(new ArrayList<>(Arrays.asList("627760015d3f4d6ace96c40a",
                        "627760015d3f4d6ace96c40b",
                        "627760015d3f4d6ace96c40c",
                        "627760015d3f4d6ace96c40d")))
                .build();

        Account accountA = Account.builder()
                .id("627760015d3f4d6ace96c40a")
                .client("Renato")
                .type("Saving")
                .balance(50.0)
                .build();

        Account accountB = Account.builder()
                .id("627760015d3f4d6ace96c40b")
                .client("Renato")
                .type("Saving")
                .balance(30.0)
                .build();

        Account accountC = Account.builder()
                .id("627760015d3f4d6ace96c40c")
                .client("Renato")
                .type("Saving")
                .balance(20.0)
                .build();

        Account accountD = Account.builder()
                .id("627760015d3f4d6ace96c40d")
                .client("Renato")
                .type("Saving")
                .balance(10.0)
                .build();

        when(debitCardRepository.findById(movementRequest.getAccount()))
                .thenReturn(Mono.just(debitCard));

        when(accountRepository.findById(accountA.getId()))
                .thenReturn(Mono.just(accountA));

        when(accountRepository.findById(accountB.getId()))
                .thenReturn(Mono.just(accountB));

        when(accountRepository.findById(accountC.getId()))
                .thenReturn(Mono.just(accountC));

        when(accountRepository.findById(accountD.getId()))
                .thenReturn(Mono.just(accountD));

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(accountA));

        //New movement: Movement(id=null, type=pay, amount=50.0, transactionFee=0.0, date=2022-05-23, account=627760015d3f4d6ace96c40a)

        Movement movementA = Movement.builder()
                .account("627760015d3f4d6ace96c40a")
                .type("pay")
                .amount(50.0)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();

        Movement movementA2 = Movement.builder()
                .account("627760015d3f4d6ace96c40a")
                .type("pay")
                .amount(30.0)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();

        Movement movementB = Movement.builder()
                .account("627760015d3f4d6ace96c40b")
                .type("pay")
                .amount(30.0)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();

        Movement movementC = Movement.builder()
                .account("627760015d3f4d6ace96c40c")
                .type("pay")
                .amount(20.0)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();

        Movement movementD = Movement.builder()
                .account("627760015d3f4d6ace96c40d")
                .type("pay")
                .amount(10.0)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();

        when(movementRepository.save(movementA))
                .thenReturn(Mono.just(movementA));

        when(movementRepository.save(movementB))
                .thenReturn(Mono.just(movementB));

        when(movementRepository.save(movementC))
                .thenReturn(Mono.just(movementC));

        when(movementRepository.save(movementD))
                .thenReturn(Mono.just(movementD));

        List<MovementResponse> movementResponseList = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard/movement")
                        .build())
                .bodyValue(movementRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void updateDebitCard() {
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_ID)
                .build();

        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        when(debitCardRepository.save(debitCard))
                .thenReturn(Mono.just(debitCard));

        DebitCardResponse debitCardResponse = client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard/{id}")
                        .build(CARD_ID))
                .bodyValue(debitCardRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DebitCardResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void deleteDebitCard() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        when(debitCardRepository.delete(debitCard))
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard/{id}")
                        .build(CARD_ID))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteAllDebitCards() {
        when(debitCardRepository.deleteAll())
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getLastMovements(){
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>(Arrays.asList(CARD_MAIN_ACCOUNT)))
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("pay")
                .build();

        when(movementRepository.findByAccount(CARD_MAIN_ACCOUNT))
                .thenReturn(Flux.just(movement));

        List<MovementResponse> movementResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/debitCard/{id}/movements")
                        .build(CARD_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getBalanceOfMainAccount(){
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>(Arrays.asList(CARD_MAIN_ACCOUNT)))
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        Account account = Account.builder()
                .id(CARD_MAIN_ACCOUNT)
                .client(CARD_NUMBER)
                .type("Saving")
                .balance(200.0)
                .build();

        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.just(account));

        String result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/debitCard/{id}/balance")
                        .build(CARD_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
    }
}