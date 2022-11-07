package com.nttdata.bootcamp.accountsservice.utils;

import com.nttdata.bootcamp.accountsservice.configuration.CurrentAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.configuration.FixedDepositAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.configuration.SavingAccountConfiguration;
import com.nttdata.bootcamp.accountsservice.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountUtilsTest {

    private static final String ACCOUNT_ID = "627760015d3f4d6ace96c44b";
    private static final String ACCOUNT_TYPE = "Saving";
    private static final String ACCOUNT_CLIENT = "627718aff4256e7261ae367f";
    private static final Double ACCOUNT_BALANCE = 100.0;
    private static final String ACCOUNT_CLIENT_TYPE = "Personal";
    private static final String ACCOUNT_CLIENT_PROFILE = "";
    private static final Double ACCOUNT_MAINTENANCE_FEE = 0.0;
    private static final Integer ACCOUNT_MOVEMENTS_LIMIT = 5;
    private static final List<String> ACCOUNT_HOLDERS = null;
    private static final List<String> ACCOUNT_SIGNERS = null;
    private static final Integer ACCOUNT_MOVEMENT_DAY = null;

    @Mock
    private CurrentAccountConfiguration currentAccountConfiguration;
    @Mock
    private FixedDepositAccountConfiguration fixedDepositAccountConfiguration;
    @Mock
    private SavingAccountConfiguration savingAccountConfiguration;

    @InjectMocks
    private AccountUtils accountUtils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setMaintenanceFee_saving() {
        when(currentAccountConfiguration.getMaintenanceFee())
                .thenReturn(10.50);

        when(fixedDepositAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        when(savingAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMaintenanceFee(account);

        assertEquals(0.0,account.getMaintenanceFee());
    }

    @Test
    void setMaintenanceFee_current_profileDefault() {
        when(currentAccountConfiguration.getMaintenanceFee())
                .thenReturn(10.50);

        when(fixedDepositAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        when(savingAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Current")
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMaintenanceFee(account);

        assertEquals(10.5,account.getMaintenanceFee());
    }

    @Test
    void setMaintenanceFee_current_profilePyme() {
        when(currentAccountConfiguration.getMaintenanceFee())
                .thenReturn(10.50);

        when(fixedDepositAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        when(savingAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Current")
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile("PYME")
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMaintenanceFee(account);

        assertEquals(0.0,account.getMaintenanceFee());
    }

    @Test
    void setMaintenanceFee_fixed() {
        when(currentAccountConfiguration.getMaintenanceFee())
                .thenReturn(10.50);

        when(fixedDepositAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        when(savingAccountConfiguration.getMaintenanceFee())
                .thenReturn(0.0);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Fixed Deposit")
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMaintenanceFee(account);

        assertEquals(0.0,account.getMaintenanceFee());
    }

    @Test
    void setMovementsLimit_saving() {

        when(fixedDepositAccountConfiguration.getMovementsLimit())
                .thenReturn(1);

        when(savingAccountConfiguration.getMovementsLimit())
                .thenReturn(5);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type(ACCOUNT_TYPE)
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMovementsLimit(account);

        assertEquals(5,account.getMovementsLimit());

    }

    @Test
    void setMovementsLimit_current() {

        when(fixedDepositAccountConfiguration.getMovementsLimit())
                .thenReturn(1);

        when(savingAccountConfiguration.getMovementsLimit())
                .thenReturn(5);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Current")
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMovementsLimit(account);

        assertEquals(Integer.MAX_VALUE,account.getMovementsLimit());

    }

    @Test
    void setMovementsLimit_fixed() {

        when(fixedDepositAccountConfiguration.getMovementsLimit())
                .thenReturn(1);

        when(savingAccountConfiguration.getMovementsLimit())
                .thenReturn(5);

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .type("Fixed Deposit")
                .client(ACCOUNT_CLIENT)
                .balance(ACCOUNT_BALANCE)
                .clientType(ACCOUNT_CLIENT_TYPE)
                .clientProfile(ACCOUNT_CLIENT_PROFILE)
                .maintenanceFee(ACCOUNT_MAINTENANCE_FEE)
                .movementsLimit(ACCOUNT_MOVEMENTS_LIMIT)
                .holders(ACCOUNT_HOLDERS)
                .signers(ACCOUNT_SIGNERS)
                .movementDay(ACCOUNT_MOVEMENT_DAY)
                .build();

        accountUtils.setMovementsLimit(account);

        assertEquals(1,account.getMovementsLimit());

    }
}