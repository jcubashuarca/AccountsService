package com.nttdata.bootcamp.accountsservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("fixed-deposit-account")
@Getter @Setter
public class FixedDepositAccountConfiguration {
    private Integer movementsLimit;
    private Double maintenanceFee;
    private Double transactionFee;
}
