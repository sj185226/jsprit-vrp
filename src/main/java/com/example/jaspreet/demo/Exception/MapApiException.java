package com.example.jaspreet.demo.Exception;

public class MapApiException extends Exception {
    private String msg;

    public MapApiException(int code, String error, String detail) {
        super();
        msg = code + "-> " + error + ": " + detail;
    }

    public String getMessage() {
        return msg;
    }
}
