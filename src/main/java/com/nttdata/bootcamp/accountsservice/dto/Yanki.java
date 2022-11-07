package com.nttdata.bootcamp.accountsservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class Yanki {
    private String id;
    private Long from;
    private Long to;
    private Double amount;
    private LocalDate date;
}
