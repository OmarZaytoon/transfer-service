package com.service.transfer.exceptions;

import com.service.transfer.dto.ErrorDto;

public class EntityNotFoundException extends TransferException {

    public EntityNotFoundException() {
        super(new ErrorDto(ErrorEnum.ENTITY_NOT_FOUND));
    }

}
