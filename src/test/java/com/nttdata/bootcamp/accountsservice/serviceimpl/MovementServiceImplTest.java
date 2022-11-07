package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import com.nttdata.bootcamp.accountsservice.exception.movement.MovementCreationException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.utils.MovementUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MovementServiceImplTest {
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
    private static final String ACCOUNT_CLIENT_NAME = "Renato";
    private static final String ACCOUNT_CLIENT_DOCUMENT_TYPE = "DNI";
    private static final Long ACCOUNT_CLIENT_DOCUMENT_NUMBER = 71318990L;
    @Mock
    private MovementRepository movementRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MovementUtils movementUtils;

    @InjectMocks
    private MovementServiceImpl movementService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
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

        StepVerifier.create(movementService.getAll())
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
    }

    @Test
    void getById() {
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

        StepVerifier.create(movementService.getById("627760015d3f4d6ace96c44b"))
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
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

        StepVerifier.create(movementService.getAllMovementsByAccount("627760015d3f4d6ace96c55cc"))
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
    }

    @Test
    void delete() {
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

        StepVerifier.create(movementService.delete("627760015d3f4d6ace96c44b"))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(movementRepository.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(movementService.deleteAll())
                .verifyComplete();
    }

    @Test
    void save() {
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

        StepVerifier.create(movementService.save(movementRequest))
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
    }

    @Test
    void save_notFoundAccount() {
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
                .thenReturn(Mono.empty());

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

        StepVerifier.create(movementService.save(movementRequest))
                .verifyError(MovementCreationException.class);
    }


    @Test
    void save_FixedAccount() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Fixed Deposit")
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

        StepVerifier.create(movementService.save(movementRequest))
                .verifyError(MovementCreationException.class);
    }

    @Test
    void save_account_limitMovements() {
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
                .amount(200.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("withdraw")
                .build();

        MovementRequest movementRequest = MovementRequest.builder()
                .account(ACCOUNT_ID)
                .amount(200.0)
                .type("withdraw")
                .build();

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        when(movementRepository.save(any()))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementService.save(movementRequest))
                .verifyError(MovementCreationException.class);
    }


    @Test
    void update() {
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

        StepVerifier.create(movementService.update("627760015d3f4d6ace96c44b",movementRequest))
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
    }

    @Test
    void update_notFoundMovement() {
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
                .thenReturn(Mono.empty());

        when(movementRepository.findByAccountAndDateBetween(ACCOUNT_ID,firstOfMonth,last))
                .thenReturn(Flux.just(movement));

        when(movementRepository.save(any()))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementService.update("627760015d3f4d6ace96c44b",movementRequest))
                .verifyError(MovementCreationException.class);
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

        StepVerifier.create(movementService.getReportOfDailyBalance(ACCOUNT_ID))
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getAllReportsByClient() {

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

        StepVerifier.create(movementService.getAllReportsByClient(ACCOUNT_CLIENT))
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getAllFeesByAccountAndPeriod() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate last = currentMonth.atEndOfMonth();

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

        StepVerifier.create(movementService.getAllFeesByAccountAndPeriod(ACCOUNT_ID,firstOfMonth,last))
                .expectNextCount(1)
                .verifyComplete();
    }
}