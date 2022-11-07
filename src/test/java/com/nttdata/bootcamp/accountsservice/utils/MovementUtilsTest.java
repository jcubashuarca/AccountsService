package com.nttdata.bootcamp.accountsservice.utils;

import com.nttdata.bootcamp.accountsservice.dto.MovementRequest;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MovementUtilsTest {
    @InjectMocks
    private MovementUtils movementUtils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDifferenceMovement_withdraw() {

        Movement update = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        MovementRequest current = MovementRequest.builder()
                .account("627760015d3f4d6ace96c55cc")
                .amount(50.0)
                .type("deposit")
                .build();

        Movement diff = Movement.builder()
                .id(update.getId())
                .account(current.getAccount())
                .amount(50.0)
                .date(update.getDate())
                .transactionFee(update.getTransactionFee())
                .type("withdraw")
                .build();

        Movement diffTest = movementUtils.createDifferenceMovement(update,current);

        assertEquals(diffTest,diff);

    }

    @Test
    void createDifferenceMovement_deposit() {

        Movement update = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(50.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        MovementRequest current = MovementRequest.builder()
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .type("deposit")
                .build();

        Movement diff = Movement.builder()
                .id(update.getId())
                .account(current.getAccount())
                .amount(50.0)
                .date(update.getDate())
                .transactionFee(update.getTransactionFee())
                .type("deposit")
                .build();

        Movement diffTest = movementUtils.createDifferenceMovement(update,current);

        assertEquals(diffTest,diff);

    }

    @ParameterizedTest
    @CsvSource({
            "Saving, 5.0",
            "Current, 0.0",
            "Fixed Deposit, 4.0",
    })
    void setTransactionFee(String type, double fee) {

        Movement movement = Movement.builder()
                .id("627760015d3f4d6ace96c44b")
                .account("627760015d3f4d6ace96c55cc")
                .amount(100.0)
                .date(LocalDate.now())
                .transactionFee(0.0)
                .type("deposit")
                .build();

        movementUtils.setTransactionFee(movement,type);

        assertEquals(fee, movement.getTransactionFee());

    }
}