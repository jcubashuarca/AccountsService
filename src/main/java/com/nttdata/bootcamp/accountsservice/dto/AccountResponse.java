package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.model.Account;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String id;
    private String client;
    private String clientType;
    private String clientProfile;
    private String type;
    private Double balance;
    private Double maintenanceFee;
    private Integer movementsLimit;
    private List<String> holders;
    private List<String> signers;
    private Integer movementDay;

    public static AccountResponse fromModel(Account account) {
        AccountResponseBuilder response = AccountResponse.builder()
                .id(account.getId())
                .client(account.getClient())
                .clientType(account.getClientType())
                .clientProfile(account.getClientProfile())
                .type(account.getType())
                .balance(account.getBalance())
                .maintenanceFee(account.getMaintenanceFee());

        if (!account.getType().equals("Current"))
            response.movementsLimit(account.getMovementsLimit());

        if (account.getType().equals("Fixed Deposit"))
            response.movementDay(account.getMovementDay());

        if (account.getClientType().equalsIgnoreCase("Business"))
            response.holders(account.getHolders()).signers(account.getSigners());

        return response.build();
    }
}
