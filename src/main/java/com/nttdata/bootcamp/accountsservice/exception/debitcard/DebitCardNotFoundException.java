package com.nttdata.bootcamp.accountsservice.exception.debitcard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class DebitCardNotFoundException extends RuntimeException{
    public DebitCardNotFoundException(String message) {
        super(message);
    }
}
