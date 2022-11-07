package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.exception.debitcard.DebitCardCreationException;
import com.nttdata.bootcamp.accountsservice.exception.debitcard.DebitCardNotFoundException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import com.nttdata.bootcamp.accountsservice.model.DebitCard;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import com.nttdata.bootcamp.accountsservice.repository.AccountRepository;
import com.nttdata.bootcamp.accountsservice.repository.DebitCardRepository;
import com.nttdata.bootcamp.accountsservice.repository.MovementRepository;
import com.nttdata.bootcamp.accountsservice.utils.MovementUtils;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardRequest;
import com.nttdata.bootcamp.accountsservice.dto.DebitCardResponse;
import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.dto.MovementResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
class DebitCardServiceImplTest {
    private static final String CARD_ID = "627760015d3f4d6ace96c44b";
    private static final String CARD_NUMBER = "44452-454548-8787841";
    private static final String CARD_CLIENT = "627718aff4256e7261ae367f";
    private static final String CARD_OTHER_CLIENT = "627718aff4256e7261ae367g";
    private static final String CARD_MAIN_ACCOUNT = "627760015d3f4d6ace96c44b";
    private static final String CARD_OTHER_ACCOUNT = "627760015d3f4d6ace96c44c";

    @Mock
    private DebitCardRepository debitCardRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MovementRepository movementRepository;
    @Mock
    private MovementUtils movementUtils;

    @InjectMocks
    private DebitCardServiceImpl debitCardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveWhenAccountNotExist() {
        //GIVEN
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_MAIN_ACCOUNT)
                .build();
        //WHEN
        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.empty());

        StepVerifier.create(debitCardService.save(debitCardRequest))
                //THEN
                .verifyError(DebitCardCreationException.class);
    }

    @Test
    void saveWhenAccountNotBelongToClient() {
        //GIVEN
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_MAIN_ACCOUNT)
                .build();
        //WHEN
        Account account = new Account();
        account.setClient(CARD_OTHER_CLIENT);

        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.just(account));

        StepVerifier.create(debitCardService.save(debitCardRequest))
                //THEN
                .verifyError(DebitCardCreationException.class);
    }

    @Test
    void saveWhenCardAlreadyExist() {
        //GIVEN
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_MAIN_ACCOUNT)
                .build();
        //WHEN
        Account account = new Account();
        account.setClient(CARD_CLIENT);

        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByNumber(any()))
                .thenReturn(Flux.just(new DebitCard()));

        StepVerifier.create(debitCardService.save(debitCardRequest))
                //THEN
                .verifyError(DebitCardCreationException.class);
    }

    @Test
    void saveSuccess() {
        //GIVEN
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

        StepVerifier.create(debitCardService.save(debitCardRequest))
                //THEN
                .assertNext(cardResponse -> {
                    assertNotNull(cardResponse);
                    assertEquals(CARD_ID, cardResponse.getId());
                    assertEquals(CARD_CLIENT, cardResponse.getClient());
                    assertEquals(CARD_NUMBER, cardResponse.getNumber());
                    assertEquals(CARD_MAIN_ACCOUNT, cardResponse.getMainAccount());
                    assertArrayEquals(Arrays.asList(CARD_MAIN_ACCOUNT).toArray(), cardResponse.getOptionalAccounts().toArray());
                })
                .verifyComplete();
    }

    @Test
    void linkWhenCardNotExist() {
        //GIVEN
        DebitCardRequest debitCardRequest = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_MAIN_ACCOUNT)
                .build();
        //WHEN
        Account account = new Account();
        account.setClient(CARD_CLIENT);

        when(accountRepository.findById(CARD_MAIN_ACCOUNT))
                .thenReturn(Mono.just(account));

        when(debitCardRepository.findCardByNumber(any()))
                .thenReturn(Flux.empty());

        StepVerifier.create(debitCardService.link(debitCardRequest))
                //THEN
                .verifyError(DebitCardCreationException.class);
    }

    @Test
    void linkSuccess() {
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

        StepVerifier.create(debitCardService.link(debitCardRequest))
                //THEN
                .assertNext(cardResponse -> {
                    assertNotNull(cardResponse);
                    assertEquals(CARD_ID, cardResponse.getId());
                    assertEquals(CARD_CLIENT, cardResponse.getClient());
                    assertEquals(CARD_NUMBER, cardResponse.getNumber());
                    assertEquals(CARD_MAIN_ACCOUNT, cardResponse.getMainAccount());
                    assertArrayEquals(Arrays.asList(CARD_MAIN_ACCOUNT, CARD_OTHER_ACCOUNT).toArray(), cardResponse.getOptionalAccounts().toArray());
                })
                .verifyComplete();
    }

    @Test
    void makeMovementSuccessFirst() {

        MovementRequest movementRequest = MovementRequest.builder()
                .account("627760015d3f4d6ace96c40a")
                .type("pay")
                .amount(30.0)
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

        MovementResponse movementResponseA2 = MovementResponse.fromModel(movementA2);

        StepVerifier.create(debitCardService.makeMovement(movementRequest))
                .expectNext(Collections.singletonList(movementResponseA2))
                .expectComplete();
    }

    @Test
    void makeMovementSuccess() {

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

        MovementResponse movementResponseA = MovementResponse.fromModel(movementA);
        MovementResponse movementResponseB = MovementResponse.fromModel(movementB);
        MovementResponse movementResponseC = MovementResponse.fromModel(movementC);

        StepVerifier.create(debitCardService.makeMovement(movementRequest))
                .expectNext(Arrays.asList(movementResponseA,movementResponseB,movementResponseC))
                .expectComplete()
                .verify();

        //[MovementResponse(id=null, type=pay, amount=50.0, date=2022-05-23, account=627760015d3f4d6ace96c40a, transactionFee=0.0),
        // MovementResponse(id=null, type=pay, amount=30.0, date=2022-05-23, account=627760015d3f4d6ace96c40b, transactionFee=0.0),
        // MovementResponse(id=null, type=pay, amount=20.0, date=2022-05-23, account=627760015d3f4d6ace96c40c, transactionFee=0.0)]
    }

    @Test
    void makeMovementNotEnoughMoney() {

        MovementRequest movementRequest = MovementRequest.builder()
                .account("627760015d3f4d6ace96c40a")
                .type("pay")
                .amount(200.0)
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

        StepVerifier.create(debitCardService.makeMovement(movementRequest))
                //THEN
                .verifyError(DebitCardCreationException.class);
    }

    @Test
    void getAll() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findAll())
                .thenReturn(Flux.just(debitCard));

        StepVerifier.create(debitCardService.getAll())
                .expectNext(DebitCardResponse.fromModel(debitCard))
                .verifyComplete();
    }

    @Test
    void getAllByClient() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findCardByClient(CARD_CLIENT))
                .thenReturn(Flux.just(debitCard));

        StepVerifier.create(debitCardService.getAllByClient(CARD_CLIENT))
                .expectNext(DebitCardResponse.fromModel(debitCard))
                .verifyComplete();
    }

    @Test
    void getById() {
        DebitCard debitCard = DebitCard.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .mainAccount(CARD_MAIN_ACCOUNT)
                .optionalAccounts(new ArrayList<>())
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.just(debitCard));

        StepVerifier.create(debitCardService.getById(CARD_ID))
                .expectNext(DebitCardResponse.fromModel(debitCard))
                .verifyComplete();
    }

    @Test
    void deleteNotFound() {
        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(debitCardService.delete(CARD_ID))
                .verifyError(DebitCardNotFoundException.class);
    }

    @Test
    void deleteFound() {
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

        StepVerifier.create(debitCardService.delete(CARD_ID))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        when(debitCardRepository.deleteAll())
                .thenReturn(Mono.empty());

        StepVerifier.create(debitCardService.deleteAll())
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        DebitCardRequest debitCard = DebitCardRequest.builder()
                .number(CARD_NUMBER)
                .client(CARD_CLIENT)
                .account(CARD_ID)
                .build();

        when(debitCardRepository.findById(CARD_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(debitCardService.update(CARD_ID,debitCard))
                .verifyError(DebitCardNotFoundException.class);
    }

    @Test
    void updateFound() {
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

        StepVerifier.create(debitCardService.update(CARD_ID,debitCardRequest))
                .expectNext(DebitCardResponse.fromModel(debitCard))
                .verifyComplete();
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

        StepVerifier.create(debitCardService.getLastMovements(CARD_ID))
                .expectNext(MovementResponse.fromModel(movement))
                .verifyComplete();
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

        StepVerifier.create(debitCardService.getBalanceOfMainAccount(CARD_ID))
                .expectNext(200.0)
                .verifyComplete();
    }
}