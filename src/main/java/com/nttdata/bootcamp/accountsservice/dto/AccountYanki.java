package com.nttdata.bootcamp.accountsservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class AccountYanki {
    private String id;
    private String documentType;
    private String documentNumber;
    private Long phone;
    private String IMEI;
    private String email;
    private String debitCard;
    private Double balance;
}
