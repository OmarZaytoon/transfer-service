package com.service.transfer.dto;

import com.service.transfer.exceptions.ErrorEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ErrorDto {
    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    public ErrorDto(ErrorEnum errorEnum) {
        this.errorCode = errorEnum.getErrorCode();
        this.errorMessage = errorEnum.getErrorMessage();
        this.status = errorEnum.getHttpStatus();
    }
}
