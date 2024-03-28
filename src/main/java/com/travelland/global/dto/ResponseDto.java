package com.travelland.global.dto;

public class ResponseDto<T> {
    private int status;
    private String msg;
    private T data;

    public ResponseDto(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResponseDto(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
