package com.nttdata.bootcamp.accountsservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Document(collection="transfers")
public class Transfer {
    @Id
    private String id;
    @NonNull
    private String from;
    @NonNull
    private String to;
    @NonNull
    private Double amount;
    private LocalDate date;
}
