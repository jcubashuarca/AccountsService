package com.nttdata.bootcamp.accountsservice.utils;

import com.nttdata.bootcamp.accountsservice.configuration.CurrentAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.configuration.FixedDepositAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.configuration.SavingAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class AccountUtils {

    private CurrentAccountConfiguration currentAccountConfiguration;
    private FixedDepositAccountConfiguration fixedDepositAccountConfiguration;
    private SavingAccountConfiguration savingAccountConfiguration;

    public void setMaintenanceFee(Account account){
        switch (account.getType()){
            case "Current":
                if (account.getClientProfile().equalsIgnoreCase("PYME")){
                    account.setMaintenanceFee(0.0);
                }else{
                    account.setMaintenanceFee(currentAccountConfiguration.getMaintenanceFee());
                }
                break;
            case "Saving":
                account.setMaintenanceFee(savingAccountConfiguration.getMaintenanceFee());
                break;
            case "Fixed Deposit":
                account.setMaintenanceFee(fixedDepositAccountConfiguration.getMaintenanceFee());
                break;
            default:
                break;
        }
    }

    public void setMovementsLimit(Account account){
        switch (account.getType()){
            case "Current":
                account.setMovementsLimit(Integer.MAX_VALUE);
                break;
            case "Saving":
                account.setMovementsLimit(savingAccountConfiguration.getMovementsLimit());
                break;
            case "Fixed Deposit":
                account.setMovementsLimit(fixedDepositAccountConfiguration.getMovementsLimit());
                break;
            default:
                break;
        }
    }


}
