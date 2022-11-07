package com.nttdata.bootcamp.accountsservice.exception.debitcard;

import com.nttdata.bootcamp.accountsservice.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DebitCardExceptionHandler {

    @ExceptionHandler(DebitCardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleAccountNotFoundException(DebitCardNotFoundException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(DebitCardCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleAccountCreationException(DebitCardCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

}
