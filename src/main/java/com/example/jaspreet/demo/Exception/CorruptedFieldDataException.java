package com.example.jaspreet.demo.Exception;

public class CorruptedFieldDataException extends Exception {
    private String msg;

    public CorruptedFieldDataException(String file, int record) {
        super();
        msg = "CorruptedFieldData: Unreadable Data in " + file + " file at " + record;
    }

    public String getMessage() {
        return msg;
    }
}
