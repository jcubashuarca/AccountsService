package com.nttdata.bootcamp.accountsservice.exception.transfer;

import com.nttdata.bootcamp.accountsservice.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TransferExceptionHandler {

    @ExceptionHandler(TransferNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleMovementNotFoundException(TransferNotFoundException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(TransferCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleTransferCreationException(TransferCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

}
