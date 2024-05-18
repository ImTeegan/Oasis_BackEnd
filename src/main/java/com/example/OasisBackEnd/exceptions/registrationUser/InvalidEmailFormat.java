package com.example.OasisBackEnd.exceptions.registrationUser;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidEmailFormat extends RuntimeException{
    public InvalidEmailFormat() {
        super("Invalid email format");
    }
}
