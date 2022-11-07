package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.model.DebitCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DebitCardResponse {

    private String id;
    private String number;
    private String client;
    private String mainAccount;
    private List<String> optionalAccounts;

    public static DebitCardResponse fromModel(DebitCard debitCard) {
        return DebitCardResponse.builder()
                .id(debitCard.getId())
                .number(debitCard.getNumber())
                .client(debitCard.getClient())
                .mainAccount(debitCard.getMainAccount())
                .optionalAccounts(debitCard.getOptionalAccounts())
                .build();
    }
}
