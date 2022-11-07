package com.nttdata.bootcamp.accountsservice.exception.movement;

import com.nttdata.bootcamp.accountsservice.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MovementExceptionHandler {

    @ExceptionHandler(MovementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleMovementNotFoundException(MovementNotFoundException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(MovementCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMovementCreationException(MovementCreationException ex) {
        return ExceptionResponse.builder().message(ex.getMessage()).build();
    }

}
