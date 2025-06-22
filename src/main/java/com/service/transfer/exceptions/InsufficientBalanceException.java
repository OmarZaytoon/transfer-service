package com.service.transfer.exceptions;

import com.service.transfer.dto.ErrorDto;

public class InsufficientBalanceException extends TransferException{
    public InsufficientBalanceException() {
        super(new ErrorDto(ErrorEnum.INSUFFICIENT_BALANCE));
    }
}
