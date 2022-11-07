package com.nttdata.bootcamp.accountsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard extends Credit {
    private String number;
    private int billingDay;
}
