package com.nttdata.bootcamp.accountsservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Document(collection="movements")
public class Movement {
    @Id
    private String id;
    private String type;
    private Double amount;
    private Double transactionFee;
    private LocalDate date;
    @NonNull
    private String account;

    public Double getAmountSigned(){
        Double amountSigned = type.equalsIgnoreCase("withdraw")||type.equalsIgnoreCase("Transfer Out")?-1*amount:amount;
        return amountSigned - transactionFee;
    }

    public int getDayOfMovement(){
        return date.getDayOfMonth();
    }
}
