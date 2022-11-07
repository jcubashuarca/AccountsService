package com.nttdata.bootcamp.accountsservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class LinkRequest {
    private Long phone;
    private String debitCard;
    private String state;
    private Double amount;
}
