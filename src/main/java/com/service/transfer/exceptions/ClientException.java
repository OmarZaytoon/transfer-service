package com.service.transfer.exceptions;

import com.service.transfer.dto.ErrorDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends TransferException {
    private final HttpStatus status;

    public ClientException(HttpStatus status) {
        super(new ErrorDto(String.format("CLIENT-%d",status.value()),status.getReasonPhrase(),HttpStatus.SERVICE_UNAVAILABLE));
        this.status = status;
    }
    public ClientException(HttpStatus status, String message) {
        super(new ErrorDto(String.format("CLIENT-%d",status.value()),message,HttpStatus.SERVICE_UNAVAILABLE));
        this.status = status;
    }

    public ClientException(String errorMsg) {
        super(new ErrorDto("CLIENT-UNKNOWN",errorMsg,HttpStatus.SERVICE_UNAVAILABLE));
        status=null;
    }


}
