package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.TransferRequest;
import com.nttdata.bootcamp.accountsservice.dto.TransferResponse;
import com.nttdata.bootcamp.accountsservice.exception.transfer.TransferCreationException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.model.Transfer;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

class TransferServiceImplTest {
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
    private TransferRepository transferRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MovementRepository movementRepository;
    @InjectMocks
    private TransferServiceImpl transferService;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findAll())
                .thenReturn(Flux.just(transfer));

        StepVerifier.create(transferService.getAll())
                .expectNext(TransferResponse.fromModel(transfer))
                .verifyComplete();
    }

    @Test
    void getAllByAccount() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findByFromOrTo("6283def0629e3030b851d919","6283def0629e3030b851d919"))
                .thenReturn(Flux.just(transfer));

        StepVerifier.create(transferService.getAllByAccount("6283def0629e3030b851d919"))
                .expectNext(TransferResponse.fromModel(transfer))
                .verifyComplete();
    }

    @Test
    void getById() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findById("6283df74629e3030b851d923"))
                .thenReturn(Mono.just(transfer));

        StepVerifier.create(transferService.getById("6283df74629e3030b851d923"))
                .expectNext(TransferResponse.fromModel(transfer))
                .verifyComplete();
    }

    @Test
    void delete() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findById("6283df74629e3030b851d923"))
                .thenReturn(Mono.just(transfer));

        when(transferRepository.delete(transfer))
                .thenReturn(Mono.empty());

        StepVerifier.create(transferService.delete("6283df74629e3030b851d923"))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(transferRepository.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(transferService.deleteAll())
                .verifyComplete();
    }

    @Test
    void save() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(20.0)
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        Account from = Account.builder()
                .id("6283def0629e3030b851d919")
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

        Account to = Account.builder()
                .id("6283df00629e3030b851d91a")
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

        when(accountRepository.findById("6283def0629e3030b851d919"))
                .thenReturn(Mono.just(from));

        when(accountRepository.findById("6283df00629e3030b851d91a"))
                .thenReturn(Mono.just(to));

        Movement movement1 = new Movement(null,
                "Transfer Out",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                from.getId());

        Movement movement2 = new Movement(null,
                "Transfer In",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                to.getId());

        when(movementRepository.save(movement1))
                .thenReturn(Mono.just(movement1));

        when(movementRepository.save(movement2))
                .thenReturn(Mono.just(movement2));

        from.setBalance(from.getBalance() - transferRequest.getAmount());
        to.setBalance(to.getBalance() + transferRequest.getAmount());

        when(accountRepository.save(from))
                .thenReturn(Mono.just(from));

        when(accountRepository.save(to))
                .thenReturn(Mono.just(to));

        when(transferRepository.save(transferRequest.toModel()))
                .thenReturn(Mono.just(transferRequest.toModel()));

        StepVerifier.create(transferService.save(transferRequest))
                .expectNext(TransferResponse.fromModel(transferRequest.toModel()))
                .verifyComplete();
    }

    @Test
    void save_not_enough_balance() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(200.0)
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        Account from = Account.builder()
                .id("6283def0629e3030b851d919")
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

        Account to = Account.builder()
                .id("6283df00629e3030b851d91a")
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

        when(accountRepository.findById("6283def0629e3030b851d919"))
                .thenReturn(Mono.just(from));

        when(accountRepository.findById("6283df00629e3030b851d91a"))
                .thenReturn(Mono.just(to));

        Movement movement1 = new Movement(null,
                "Transfer Out",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                from.getId());

        Movement movement2 = new Movement(null,
                "Transfer In",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                to.getId());

        when(movementRepository.save(movement1))
                .thenReturn(Mono.just(movement1));

        when(movementRepository.save(movement2))
                .thenReturn(Mono.just(movement2));

        from.setBalance(from.getBalance() - transferRequest.getAmount());
        to.setBalance(to.getBalance() + transferRequest.getAmount());

        when(accountRepository.save(from))
                .thenReturn(Mono.just(from));

        when(accountRepository.save(to))
                .thenReturn(Mono.just(to));

        when(transferRepository.save(transferRequest.toModel()))
                .thenReturn(Mono.just(transferRequest.toModel()));

        StepVerifier.create(transferService.save(transferRequest))
                .verifyError(TransferCreationException.class);
    }

    @Test
    void save_different_client() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(20.0)
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        Account from = Account.builder()
                .id("6283def0629e3030b851d919")
                .type(ACCOUNT_TYPE)
                .client("6283def0629e3030b851d888")
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        Account to = Account.builder()
                .id("6283df00629e3030b851d91a")
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

        when(accountRepository.findById("6283def0629e3030b851d919"))
                .thenReturn(Mono.just(from));

        when(accountRepository.findById("6283df00629e3030b851d91a"))
                .thenReturn(Mono.just(to));

        Movement movement1 = new Movement(null,
                "Transfer Out",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                from.getId());

        Movement movement2 = new Movement(null,
                "Transfer In",
                transferRequest.getAmount(),
                0.0,
                LocalDate.now(),
                to.getId());

        when(movementRepository.save(movement1))
                .thenReturn(Mono.just(movement1));

        when(movementRepository.save(movement2))
                .thenReturn(Mono.just(movement2));

        from.setBalance(from.getBalance() - transferRequest.getAmount());
        to.setBalance(to.getBalance() + transferRequest.getAmount());

        when(accountRepository.save(from))
                .thenReturn(Mono.just(from));

        when(accountRepository.save(to))
                .thenReturn(Mono.just(to));

        when(transferRepository.save(transferRequest.toModel()))
                .thenReturn(Mono.just(transferRequest.toModel()));

        StepVerifier.create(transferService.save(transferRequest))
                .expectNext(TransferResponse.fromModel(transferRequest.toModel()))
                .verifyComplete();
    }

    @Test
    void update() {

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(20.0)
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.save(transferRequest.toModel()))
                .thenReturn(Mono.just(transferRequest.toModel()));

        StepVerifier.create(transferService.update("6283def0629e3030b851d919",transferRequest))
                .expectNext(TransferResponse.fromModel(transferRequest.toModel()))
                .verifyComplete();
    }
}