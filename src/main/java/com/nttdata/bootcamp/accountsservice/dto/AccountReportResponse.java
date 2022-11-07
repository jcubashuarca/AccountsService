package com.nttdata.bootcamp.accountsservice.dto;

import com.nttdata.bootcamp.accountsservice.model.Account;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AccountReportResponse extends AccountResponse {
    private DebitCardResponse debitCard;
    private List<MovementResponse> movements;
    private List<FeeResponse> fees;
    private List<TransferResponse> transfers;

    @Builder(builderMethodName = "reportBuilder")
    public AccountReportResponse(String id, String client, String clientType, String clientProfile,
                                 String type, Double balance, Double maintenanceFee, Integer movementsLimit,
                                 List<String> holders, List<String> signers, Integer movementDay,
                                 DebitCardResponse debitCard, List<MovementResponse> movements, List<FeeResponse> fees,
                                 List<TransferResponse> transfers) {
        super(id, client, clientType, clientProfile, type, balance, maintenanceFee, movementsLimit, holders, signers, movementDay);
        this.debitCard = debitCard;
        this.movements = movements;
        this.fees = fees;
        this.transfers = transfers;
    }

    public static AccountReportResponse fromModel(Account account) {
        return AccountReportResponse.reportBuilder()
                .id(account.getId())
                .client(account.getClient())
                .clientType(account.getClientType())
                .clientProfile(account.getClientProfile())
                .type(account.getType())
                .balance(account.getBalance())
                .maintenanceFee(account.getMaintenanceFee())
                .movementsLimit(account.getMovementsLimit())
                .movementDay(account.getMovementDay())
                .holders(account.getHolders())
                .build();
    }
}
