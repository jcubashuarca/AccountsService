package com.nttdata.bootcamp.accountsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageKafka {
    private String type;
    private String document;
    private String number;
    private Boolean success;
    private String message;
}
