package com.service.transfer.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorEnum {
    SAME_IDENTITY("TR-006","UnAllowed To Transfer to Same Account",HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE("TR-007","Insufficient Balance",HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND("TR-005","Entity Not Found",HttpStatus.BAD_REQUEST),
    GENERAL_ERROR("TR-004","General Error",HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_VALIDATION_ERROR("TR-003","General Field Constraint Error",HttpStatus.BAD_REQUEST),
    ;


    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    ErrorEnum(String errorCode, String errorMessage, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
