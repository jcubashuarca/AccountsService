package com.nttdata.bootcamp.accountsservice.model;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Credit {
    private String id;
    private String clientId;
    private double amount;
    private int paymentDay;
    private double tcea;
    private double balance;
}
