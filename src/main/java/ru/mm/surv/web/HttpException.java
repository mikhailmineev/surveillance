package ru.mm.surv.web;

public class HttpException extends RuntimeException {

    private int code;

    public HttpException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
