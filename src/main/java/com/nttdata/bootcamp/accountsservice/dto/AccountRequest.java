package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.exception.account.AccountCreationException;
import com.nttdata.bootcamp.accountsservice.model.Account;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountRequest {

    private static final String FIXED_DEPOSIT = "Fixed Deposit";
    private static final String CURRENT = "Current";
    private static final String SAVING = "Saving";

    private String client;
    private String clientType;
    private String clientProfile;
    private String type;

    private String number;
    private Double balance;
    private List<String> holders;
    private List<String> signers;
    private Integer movementDay;
    private Double minimumAmount;

    public Account toModel() {

        if (Objects.isNull(type) || Objects.isNull(balance) || Objects.isNull(client))
            throw new AccountCreationException("Type, Balance and Client are mandatory attributes");

        if (!type.equals(SAVING) && !type.equals(CURRENT) && !type.equals(FIXED_DEPOSIT))
            throw new AccountCreationException("The type of account must have a value from: 'Saving','Current' or 'Fixed Deposit'");

        if (balance < 0)
            throw new AccountCreationException("The balance could not be less than 0 or null");

        if (clientType.equalsIgnoreCase("Business") &&
                (type.equals(SAVING) || type.equals(FIXED_DEPOSIT) || Objects.isNull(holders) || holders.isEmpty()))
            throw new AccountCreationException("A business account can only be of 'Current' type and must have at least one account holder");

        if (type.equals(FIXED_DEPOSIT) && Objects.isNull(movementDay))
            throw new AccountCreationException("If account type is 'Fixed Deposit' must have a Movement Day attribute");

        if(clientProfile.equalsIgnoreCase("VIP") && type.equals(SAVING) && Objects.isNull(minimumAmount))
            throw new AccountCreationException("If client type is 'VIP' the Saving account must have a minimum amount");

        return Account.builder()
                .client(this.client)
                .clientType(this.clientType)
                .clientProfile(this.clientProfile)
                .type(this.type)
                .balance(this.balance)
                .number(this.number)
                .holders(this.holders)
                .signers(this.signers)
                .movementDay(this.movementDay)
                .build();
    }

}
