package com.service.transfer.exceptions;

import com.service.transfer.dto.ErrorDto;
import lombok.Getter;


@Getter
public class TransferException extends RuntimeException {
    private final ErrorDto errorDto;

    public TransferException(ErrorDto errorDto) {
        super(String.format("Something went in Transfer errorCode:%s, message: %s",errorDto.getErrorCode(),errorDto.getErrorMessage()));
        this.errorDto = errorDto;
    }

}
