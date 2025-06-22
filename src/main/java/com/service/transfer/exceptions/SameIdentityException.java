package com.service.transfer.exceptions;

import com.service.transfer.dto.ErrorDto;

public class SameIdentityException extends TransferException{
    public SameIdentityException() {
        super(new ErrorDto(ErrorEnum.SAME_IDENTITY));
    }
}
