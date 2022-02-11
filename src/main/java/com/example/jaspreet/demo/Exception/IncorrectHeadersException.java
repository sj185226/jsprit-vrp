package com.example.jaspreet.demo.Exception;

public class IncorrectHeadersException extends Exception {

    private String msg;

    public IncorrectHeadersException(String headerType) {
        super();
        msg = "IncorrectHeadersException: Incorrect headers found in " + headerType + " don't match with requirements";
    }

    public String getMessage() {
        return msg;
    }
}
