package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.exception.movement.MovementCreationException;
import com.nttdata.bootcamp.accountsservice.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MovementRequest {

    private String type;
    private String account;
    private Double amount;

    public Movement toModel() {
        if (Objects.isNull(type) || Objects.isNull(account) || Objects.isNull(amount))
            throw new MovementCreationException("Type, Account and Amount are mandatory attributes");

        if (!type.equalsIgnoreCase("withdraw") && !type.equalsIgnoreCase("deposit"))
            throw new MovementCreationException("The type of movement must have a value from: 'Withdraw' or 'Deposit'");

        return Movement.builder()
                .type(this.type)
                .account(this.account)
                .amount(this.amount)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();
    }

    public Movement toModelWithAmount(Double balance) {
        return Movement.builder()
                .type(this.type)
                .account(this.account)
                .amount(balance)
                .transactionFee(0.0)
                .date(LocalDate.now())
                .build();
    }

    public Double getAmountSigned() {
        return type.equals("withdraw")||
                type.equals("withdraw debit")||
                type.equals("pay")||type.equals("yanki out") ? -1 * amount : amount;
    }

}
