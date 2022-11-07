package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.exception.ExceptionResponse;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Client;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import com.nttdata.bootcamp.accountsservice.utils.AccountUtils;
import com.nttdata.bootcamp.accountsservice.utils.WebClientUtils;
import com.nttdata.bootcamp.accountsservice.dto.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountControllerTest {
    private static final String ACCOUNT_ID = "627760015d3f4d6ace96c44b";
    private static final String ACCOUNT_TYPE = "Saving";
    private static final String ACCOUNT_CLIENT = "627718aff4256e7261ae367f";
    private static final Double ACCOUNT_BALANCE = 100.0;
    private static final String ACCOUNT_CLIENT_TYPE = "Personal";
    private static final String ACCOUNT_CLIENT_PROFILE = "";
    private static final Double ACCOUNT_MAINTENANCE_FEE = 0.0;
    private static final Integer ACCOUNT_MOVEMENTS_LIMIT = 5;
    private static final List<String> ACCOUNT_HOLDERS = null;
    private static final List<String> ACCOUNT_SIGNERS = null;
    private static final Integer ACCOUNT_MOVEMENT_DAY = null;
    private static final String ACCOUNT_CLIENT_NAME = "Renato";
    private static final String ACCOUNT_CLIENT_DOCUMENT_TYPE = "DNI";
    private static final Long ACCOUNT_CLIENT_DOCUMENT_NUMBER = 71318990L;

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
    void shouldGetOneAccount() {
        //GIVEN
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.just(account));
        //WHEN
        AccountResponse accountResponse = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/{id}").build(ACCOUNT_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponse.class)
                .returnResult().getResponseBody();
        //THEN
        assertNotNull(accountResponse);
        assertEquals(ACCOUNT_ID, accountResponse.getId());
        assertEquals(ACCOUNT_TYPE, accountResponse.getType());
        assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
        assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
        assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
        assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
        assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
        assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
        assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
        assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
    }

    @Test
    void shouldReturnNotFoundWhenGetOneAccount() {
        //GIVEN
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.empty());
        //WHEN
        ExceptionResponse exceptionResponse = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/{id}").build(ACCOUNT_ID))
                .exchange()
                //THEN
                .expectStatus().isNotFound()
                .expectBody(ExceptionResponse.class)
                .returnResult().getResponseBody();

        //assertTrue(exceptionResponse.getMessage().contains(""));
    }

    @Test
    void shouldGetAllAccounts() {
        //GIVEN
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        when(accountRepository.findAll())
                .thenReturn(Flux.just(account));
        //WHEN
        List<AccountResponse> accountResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponse.class)
                .returnResult()
                .getResponseBody();

        //THEN
        assertNotNull(accountResponseList);
        AccountResponse accountResponse = accountResponseList.get(0);
        assertEquals(ACCOUNT_ID, accountResponse.getId());
        assertEquals(ACCOUNT_TYPE, accountResponse.getType());
        assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
        assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
        assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
        assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
        assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
        assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
        assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
        assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
    }

    @Test
    void shouldGetAllAccountsByClient() {
        //GIVEN
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

        when(accountRepository.findAccountByClient(ACCOUNT_CLIENT))
                .thenReturn(Flux.just(account));
        //WHEN
        List<AccountResponse> accountResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/client/{client}").build(ACCOUNT_CLIENT))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponse.class)
                .returnResult()
                .getResponseBody();

        //THEN
        assertNotNull(accountResponseList);
        AccountResponse accountResponse = accountResponseList.get(0);
        assertEquals(ACCOUNT_ID, accountResponse.getId());
        assertEquals(ACCOUNT_TYPE, accountResponse.getType());
        assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
        assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
        assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
        assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
        assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
        assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
        assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
        assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
    }

    @Test
    void getReportByAccountAndPeriod() {
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

        AccountReportResponse response = AccountReportResponse.fromModel(account);
        response.setDebitCard(new DebitCardResponse());
        response.setMovements(Collections.emptyList());
        response.setFees(Collections.emptyList());
        response.setTransfers(Collections.emptyList());

        LocalDate from = LocalDate.of(2022, 5, 17);
        LocalDate to = LocalDate.of(2022, 6, 25);

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByMainAccount(ACCOUNT_ID))
                .thenReturn(Flux.empty());

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID, from, to))
                .thenReturn(Flux.empty());

        when(transferRepository.findByFromOrToAndDateBetween(ACCOUNT_ID, ACCOUNT_ID, from, to))
                .thenReturn(Flux.empty());

        AccountReportResponse accountReportResponse = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/report/from/{yearFrom}/{monthFrom}/{dayFrom}/to/{yearTo}/{monthTo}/{dayTo}")
                        .build(ACCOUNT_ID, 2022, 5, 17, 2022, 6, 25))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountReportResponse.class)
                .returnResult()
                .getResponseBody();

        //THEN
        assertNotNull(accountReportResponse);
        assertEquals(accountReportResponse, response);
    }

    @Test
    void getReportOfDailyBalance() {
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

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        String result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/dailyBalance")
                        .build(ACCOUNT_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    void getAllFeesByAccountAndPeriod(){
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        int dayFrom = 1;
        int dayTo = last.getDayOfMonth();
        int monthFrom = currentMonth.getMonthValue();
        int monthTo = currentMonth.getMonthValue();
        int year = LocalDate.now().getYear();

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(5.0)
                .type("deposit")
                .build();

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        List<FeeResponse> result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}/feeReport/from/{yearFrom}/{monthFrom}/{dayFrom}/to/{yearTo}/{monthTo}/{dayTo}")
                        .build(ACCOUNT_ID, year, monthFrom, dayFrom, year, monthTo, dayTo))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FeeResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getAllAccountsReportByClient(){
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

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account(ACCOUNT_ID)
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        List<Map> result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/client/{client}/dailyBalance")
                        .build(ACCOUNT_CLIENT))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Map.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void saveAccount(){
        AccountRequest accountRequest = AccountRequest.builder()
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .build();

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

        Client clientAux = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile(ACCOUNT_CLIENT_PROFILE)
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(clientAux));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));

        AccountResponse result = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts")
                        .build())
                .bodyValue(accountRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void updateAccount(){
        AccountRequest accountRequest = AccountRequest.builder()
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Account accountAux = Account.builder()
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
                .thenReturn(Mono.just(accountAux));

        when(accountRepository.save(accountAux))
                .thenReturn(Mono.just(accountAux));

        AccountResponse result = client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}")
                        .build(ACCOUNT_ID))
                .bodyValue(accountRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void deleteAccount(){
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

        when(accountRepository.delete(account))
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{id}")
                        .build(ACCOUNT_ID))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteAll(){
        when(accountRepository.deleteAll())
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

}