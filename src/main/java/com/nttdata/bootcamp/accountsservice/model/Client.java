package com.nttdata.bootcamp.accountsservice.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Client {
    private String id;
    private String name;
    private String type;
    private String profile;
    private String documentType;
    private Long documentNumber;
}
