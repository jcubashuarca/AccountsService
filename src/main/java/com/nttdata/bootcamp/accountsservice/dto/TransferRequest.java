package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.exception.transfer.TransferCreationException;
import com.nttdata.bootcamp.accountsservice.model.Transfer;
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
public class TransferRequest {
    private String from;
    private String to;
    private Double amount;

    public Transfer toModel() {

        if (Objects.isNull(from) || Objects.isNull(to) || Objects.isNull(amount))
            throw new TransferCreationException("Account from, Account to and amount are mandatory attributes");

        if (amount < 0)
            throw new TransferCreationException("Amount cant be negative");

        return Transfer.builder()
                .from(this.from)
                .to(this.to)
                .amount(this.amount)
                .date(LocalDate.now())
                .build();
    }
}
