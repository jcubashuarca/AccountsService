package com.nttdata.bootcamp.accountsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Long to;
    private String status;
    private String message;

    public static Result successToSender(Yanki yanki){
        return Result.builder()
                .to(yanki.getFrom())
                .status("Success")
                .message("You make a Yanki of " + yanki.getAmount() + " to " + yanki.getTo())
                .build();
    }

    public static Result successToReceiver(Yanki yanki){
        return Result.builder()
                .to(yanki.getTo())
                .status("Success")
                .message("You received a Yanki of " + yanki.getAmount() + " from " + yanki.getFrom())
                .build();
    }


}
