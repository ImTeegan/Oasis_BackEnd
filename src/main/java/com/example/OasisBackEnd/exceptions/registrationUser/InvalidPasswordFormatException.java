package com.example.OasisBackEnd.exceptions.registrationUser;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPasswordFormatException  extends RuntimeException{
    public InvalidPasswordFormatException () {
        super("Invalid password format. Must contain: one uppercase letter, one lowcase letter, one number and must be 8 character lenght");
    }
}
