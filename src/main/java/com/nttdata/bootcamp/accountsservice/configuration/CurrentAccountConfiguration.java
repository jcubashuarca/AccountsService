package com.nttdata.bootcamp.accountsservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("current-account")
@Getter @Setter
public class CurrentAccountConfiguration {
    private double maintenanceFee;
}
