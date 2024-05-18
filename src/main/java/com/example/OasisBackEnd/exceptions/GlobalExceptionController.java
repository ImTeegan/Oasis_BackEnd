package com.example.OasisBackEnd.exceptions;

import com.example.OasisBackEnd.dtos.ResponseError;
import com.example.OasisBackEnd.exceptions.registrationUser.InvalidEmailFormat;
import com.example.OasisBackEnd.exceptions.registrationUser.InvalidPasswordFormatException;
import com.example.OasisBackEnd.exceptions.registrationUser.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler (value = {InvalidEmailFormat.class})
    protected ResponseEntity<ResponseError> handleConflict(InvalidEmailFormat ex, WebRequest request) {
        return new ResponseEntity<ResponseError>(new ResponseError(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (value = {InvalidPasswordFormatException.class})
    protected ResponseEntity<ResponseError> handleConflict(InvalidPasswordFormatException ex, WebRequest request) {
        return new ResponseEntity<ResponseError>(new ResponseError(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UserAlreadyExistsException.class })
    protected ResponseEntity<ResponseError> handleConflict(UserAlreadyExistsException ex, WebRequest request) {
        return new ResponseEntity<ResponseError>(new ResponseError(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

}
