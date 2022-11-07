package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import com.nttdata.bootcamp.accountsservice.utils.AccountUtils;
import com.nttdata.bootcamp.accountsservice.utils.MovementUtils;
import com.nttdata.bootcamp.accountsservice.utils.WebClientUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MovementControllerTest {
    private static final String ACCOUNT_ID = "627760015d3f4d6ace96c44b";
    private static final String ACCOUNT_TYPE = "Saving";
    private static final String ACCOUNT_CLIENT = "627718aff4256e7261ae367f";
    private static final Double ACCOUNT_BALANCE = 100.0;
    private static final String ACCOUNT_CLIENT_TYPE = "Personal";
    private static final String ACCOUNT_CLIENT_PROFILE = "";
    private static final String ACCOUNT_CLIENT_PROFILE_VIP = "VIP";
    private static final Double ACCOUNT_MAINTENANCE_FEE = 0.0;
    private static final Integer ACCOUNT_MOVEMENTS_LIMIT = 5;
    private static final List<String> ACCOUNT_HOLDERS = null;
    private static final List<String> ACCOUNT_SIGNERS = null;
    private static final Integer ACCOUNT_MOVEMENT_DAY = 10;

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private DebitCardRepository debitCardRepository;
    @MockBean
    private MovementRepository movementRepository;
    @MockBean
    private TransferRepository transferRepository;
    @MockBean
    private MovementUtils movementUtils;
    @MockBean
    private AccountUtils accountUtils;
    @MockBean
    private WebClientUtils webClientUtils;

    @Autowired
    WebTestClient client;

    @Test
    void getAllMovements() {
        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findAll())
                .thenReturn(Flux.just(movement));

        List<MovementResponse> debitCardResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/movement").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getAllMovementsByAccount() {
        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findByAccount("627760015d3f4d6ace96c55cc"))
                .thenReturn(Flux.just(movement));

        List<MovementResponse> debitCardResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/movement/product/{account}")
                        .build("627760015d3f4d6ace96c55cc"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getMovement() {
        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findById("627760015d3f4d6ace96c44b"))
                .thenReturn(Mono.just(movement));

        MovementResponse debitCardResponse = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/movement/{id}")
                        .build("627760015d3f4d6ace96c44b"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovementResponse.class)
                .returnResult().getResponseBody();
    }

    @Test
    void saveMovement() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.just(account));

        when(accountRepository.findAccountByClient(ACCOUNT_CLIENT))
                .thenReturn(Flux.just(account));

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(20.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        MovementRequest movementRequest = MovementRequest.builder()
                .account(ACCOUNT_ID)
                .amount(20.0)
                .type("deposit")
                .build();

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        when(movementRepository.save(any()))
                .thenReturn(Mono.just(movement));

        MovementResponse debitCardResponse = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/movement")
                        .build())
                .bodyValue(movementRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void updateMovement() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.just(account));

        when(accountRepository.findAccountByClient(ACCOUNT_CLIENT))
                .thenReturn(Flux.just(account));

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(20.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        Movement movementDiff = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(0.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        MovementRequest movementRequest = MovementRequest.builder()
                .account(ACCOUNT_ID)
                .amount(20.0)
                .type("deposit")
                .build();

        when(movementUtils.createDifferenceMovement(any(),any()))
                .thenReturn(movementDiff);

        when(movementRepository.findById("627760015d3f4d6ace96c44b"))
                .thenReturn(Mono.just(movement));

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        when(movementRepository.save(any()))
                .thenReturn(Mono.just(movement));

        MovementResponse debitCardResponse = client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/movement/{id}")
                        .build("627760015d3f4d6ace96c44b"))
                .bodyValue(movementRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovementResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void deleteMovement() {
        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findById("627760015d3f4d6ace96c44b"))
                .thenReturn(Mono.just(movement));

        when(movementRepository.delete(movement))
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/movement/{id}")
                        .build("627760015d3f4d6ace96c44b"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteAllMovements() {
        when(movementRepository.deleteAll())
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/movement")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }
}