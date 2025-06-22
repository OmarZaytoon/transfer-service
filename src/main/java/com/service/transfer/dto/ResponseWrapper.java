package com.service.transfer.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseWrapper<T> {
    private HttpStatus status;
    private List<ErrorDto> message;
    private T body;

    public ResponseWrapper(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
        this.message = new ArrayList<>();
    }

    public ResponseWrapper(HttpStatus status) {
        this.status = status;
        this.body = null;
        this.message = new ArrayList<>();
    }

    public ResponseWrapper(T body) {
        this.body = body;
        this.status = HttpStatus.OK;
        this.message = new ArrayList<>();
    }

    private ResponseWrapper(HttpStatus status, ErrorDto errorDto) {
        this.status = status;
        this.message = new ArrayList<>();
        this.message.add(errorDto);
    }

    public static <T> ResponseWrapper<T> of(T body) {
        return new ResponseWrapper<>(body);
    }



    public static <T> ResponseWrapper<T> ofError(ErrorDto errorDto) {
        return new ResponseWrapper<>(errorDto.getStatus(), errorDto);

    }

    public ResponseWrapper<T> addError(String errorCode, String errorMessage, HttpStatus status) {
        this.message.add(new ErrorDto(errorCode, errorMessage, status));
        return this;
    }

    public ResponseWrapper<T> addError(ErrorDto errorDto) {
        this.message.add(errorDto);
        return this;
    }


}
