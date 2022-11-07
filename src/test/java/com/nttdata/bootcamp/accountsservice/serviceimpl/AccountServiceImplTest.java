package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.AccountReportResponse;
import com.nttdata.bootcamp.accountsservice.dto.AccountRequest;
import com.nttdata.bootcamp.accountsservice.dto.AccountResponse;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.exception.account.AccountCreationException;
import com.nttdata.bootcamp.accountsservice.exception.account.AccountNotFoundException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Client;
import com.nttdata.bootcamp.accountsservice.model.CreditCard;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import com.nttdata.bootcamp.accountsservice.utils.AccountUtils;
import com.nttdata.bootcamp.accountsservice.utils.WebClientUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

class AccountServiceImplTest {

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
    private static final Integer ACCOUNT_MOVEMENT_DAY = null;
    private static final String ACCOUNT_CLIENT_NAME = "Renato";
    private static final String ACCOUNT_CLIENT_DOCUMENT_TYPE = "DNI";
    private static final Long ACCOUNT_CLIENT_DOCUMENT_NUMBER = 71318990L;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private DebitCardRepository debitCardRepository;
    @Mock
    private MovementRepository movementRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private AccountUtils accountUtils;
    @Mock
    private WebClientUtils webClientUtils;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateAccount() {
        //GIVEN
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

        Client client = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile(ACCOUNT_CLIENT_PROFILE)
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .assertNext(accountResponse -> {
                    assertNotNull(accountResponse);
                    assertEquals(ACCOUNT_ID, accountResponse.getId());
                    assertEquals(ACCOUNT_TYPE, accountResponse.getType());
                    assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
                    assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
                    assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
                    assertEquals(ACCOUNT_CLIENT_PROFILE, accountResponse.getClientProfile());
                    assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
                    assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
                    assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
                    assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
                    assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateAccountVIP() {
        //GIVEN
        AccountRequest accountRequest = AccountRequest.builder()
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .minimumAmount(700.0)
                .build();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE_VIP)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Client client = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile(ACCOUNT_CLIENT_PROFILE_VIP)
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(webClientUtils.getCredits(any()))
                .thenReturn(Flux.just(new CreditCard()));

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .assertNext(accountResponse -> {
                    assertNotNull(accountResponse);
                    assertEquals(ACCOUNT_ID, accountResponse.getId());
                    assertEquals(ACCOUNT_TYPE, accountResponse.getType());
                    assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
                    assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
                    assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
                    assertEquals(ACCOUNT_CLIENT_PROFILE_VIP, accountResponse.getClientProfile());
                    assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
                    assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
                    assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
                    assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
                    assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateAccountPYME() {
        //GIVEN
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
                .clientProfile("PYME")
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Client client = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile("PYME")
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(webClientUtils.getCredits(any()))
                .thenReturn(Flux.empty());

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .verifyError(AccountCreationException.class);
    }

    @Test
    void shouldNotCreateAccountVIP() {
        //GIVEN
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
                .clientProfile(ACCOUNT_CLIENT_PROFILE_VIP)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Client client = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile(ACCOUNT_CLIENT_PROFILE_VIP)
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(webClientUtils.getCredits(ACCOUNT_CLIENT))
                .thenReturn(Flux.empty());

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .verifyError(AccountCreationException.class);
    }

    @Test
    void shouldCreateAccountPYME() {
        //GIVEN
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
                .clientProfile("PYME")
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Client client = Client.builder()
                .id(ACCOUNT_CLIENT)
                .name(ACCOUNT_CLIENT_NAME)
                .type(ACCOUNT_CLIENT_TYPE)
                .profile("PYME")
                .documentType(ACCOUNT_CLIENT_DOCUMENT_TYPE)
                .documentNumber(ACCOUNT_CLIENT_DOCUMENT_NUMBER).build();

        when(webClientUtils.getClient(any()))
                .thenReturn(Mono.just(client));

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(webClientUtils.getCredits(any()))
                .thenReturn(Flux.just(new CreditCard()));

        when(accountRepository.findAccountByClientAndType(any(), any()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .assertNext(accountResponse -> {
                    assertNotNull(accountResponse);
                    assertEquals(ACCOUNT_ID, accountResponse.getId());
                    assertEquals(ACCOUNT_TYPE, accountResponse.getType());
                    assertEquals(ACCOUNT_CLIENT, accountResponse.getClient());
                    assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
                    assertEquals(ACCOUNT_CLIENT_TYPE, accountResponse.getClientType());
                    assertEquals("PYME", accountResponse.getClientProfile());
                    assertEquals(ACCOUNT_MAINTENANCE_FEE, accountResponse.getMaintenanceFee());
                    assertEquals(ACCOUNT_MOVEMENTS_LIMIT, accountResponse.getMovementsLimit());
                    assertEquals(ACCOUNT_HOLDERS, accountResponse.getHolders());
                    assertEquals(ACCOUNT_SIGNERS, accountResponse.getSigners());
                    assertEquals(ACCOUNT_MOVEMENT_DAY, accountResponse.getMovementDay());
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowAccountCreationExceptionWhenUnableToSave() {
        //GIVEN
        AccountRequest accountRequest = AccountRequest.builder()
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .build();

        when(webClientUtils.isClientWithCreditDebt(any()))
                .thenReturn(Mono.just(false));

        when(accountRepository.save(any()))
                .thenThrow(new RuntimeException("Connection Lost"));
        //WHEN
        StepVerifier.create(accountService.save(accountRequest))
                //THEN
                .verifyError(AccountCreationException.class);
    }


    @Test
    void getAll() {
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

        when(accountRepository.findAll())
                .thenReturn(Flux.just(account));

        StepVerifier.create(accountService.getAll())
                .expectNext(AccountResponse.fromModel(account))
                .verifyComplete();
    }

    @Test
    void getAllByClient() {
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

        StepVerifier.create(accountService.getAllByClient(ACCOUNT_CLIENT))
                .expectNext(AccountResponse.fromModel(account))
                .verifyComplete();
    }

    @Test
    void getReportNotFound() {

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.getReport(ACCOUNT_ID,
                        LocalDate.of(2022, 05, 17),
                        LocalDate.of(2022, 05, 25)))
                .verifyError(AccountNotFoundException.class);
    }

    @Test
    void getReportFound() {
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
        LocalDate to = LocalDate.of(2022, 5, 25);

        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByMainAccount(ACCOUNT_ID))
                .thenReturn(Flux.empty());

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,from,to))
                .thenReturn(Flux.empty());

        when(transferRepository.findByFromOrToAndDateBetween(ACCOUNT_ID,ACCOUNT_ID,from,to))
                .thenReturn(Flux.empty());

        StepVerifier.create(accountService.getReport(ACCOUNT_ID,from,to))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getById() {
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

        when(accountRepository.findById(ACCOUNT_CLIENT))
                .thenReturn(Mono.just(account));

        StepVerifier.create(accountService.getById(ACCOUNT_CLIENT))
                .expectNext(AccountResponse.fromModel(account))
                .verifyComplete();
    }

    @Test
    void deleteNotFound() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.delete(ACCOUNT_ID))
                .verifyError(AccountNotFoundException.class);
    }

    @Test
    void deleteFound() {
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

        StepVerifier.create(accountService.delete(ACCOUNT_ID))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(accountRepository.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAll())
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        AccountRequest account = AccountRequest.builder()
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();


        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.update(ACCOUNT_ID,account))
                .verifyError(AccountNotFoundException.class);
    }

    @Test
    void updateFound() {
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

        when(accountRepository.save(account))
                .thenReturn(Mono.just(account));

        StepVerifier.create(accountService.update(ACCOUNT_ID,accountRequest))
                .expectNext(AccountResponse.fromModel(account))
                .verifyComplete();
    }

}
