package com.example.jaspreet.demo.Exception;

public class EmptyMandatoryFieldException extends Exception {
    private String msg;

    public EmptyMandatoryFieldException(String file, int record) {
        super();
        msg = "EmptyMandatoryFieldException: Empty mandatory field in " + file + " file at " + record;
    }

    public String getMessage() {
        return msg;
    }
}
