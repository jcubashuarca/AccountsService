package com.nttdata.bootcamp.accountsservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("saving-account")
@Getter @Setter
public class SavingAccountConfiguration {
    private Integer movementsLimit;
    private Double maintenanceFee;
    private Double transactionFee;
}
