package com.nttdata.bootcamp.accountsservice.controller;

import com.nttdata.bootcamp.accountsservice.dto.TransferRequest;
import com.nttdata.bootcamp.accountsservice.dto.TransferResponse;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.model.Transfer;
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
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransferControllerTest {
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
    void getAllTransfers() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findAll())
                .thenReturn(Flux.just(transfer));

        List<TransferResponse> transferResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/transfer").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransferResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getAllTransfersByAccount() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findByFromOrTo("6283def0629e3030b851d919","6283def0629e3030b851d919"))
                .thenReturn(Flux.just(transfer));

        List<TransferResponse> transferResponseList = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/transfer/product/{account}")
                        .build("6283def0629e3030b851d919"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransferResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void getTransfer() {
        Transfer transfer = Transfer.builder()
                .id("6283df74629e3030b851d923")
                .amount(20.0)
                .date(LocalDate.now())
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.findById("6283df74629e3030b851d923"))
                .thenReturn(Mono.just(transfer));

        TransferResponse transferResponse = client.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts/transfer/{id}")
                        .build("6283df74629e3030b851d923"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse.class)
                .returnResult().getResponseBody();
    }

    @Test
    void saveTransfer() {
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

        TransferResponse transferResponse = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/transfer")
                        .build())
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransferResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void updateTransfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(20.0)
                .from("6283def0629e3030b851d919")
                .to("6283df00629e3030b851d91a")
                .build();

        when(transferRepository.save(transferRequest.toModel()))
                .thenReturn(Mono.just(transferRequest.toModel()));

        TransferResponse transferResponse = client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/transfer/{id}")
                        .build("6283def0629e3030b851d919"))
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void deleteTransfer() {
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

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/transfer/{id}")
                        .build("6283df74629e3030b851d923"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteAllTransfers() {
        when(transferRepository.deleteAll())
                .thenReturn(Mono.empty());

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/transfer")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }
}