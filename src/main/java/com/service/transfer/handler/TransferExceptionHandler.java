package com.service.transfer.handler;

import com.service.transfer.dto.ErrorDto;
import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.exceptions.ClientException;
import com.service.transfer.exceptions.ErrorEnum;
import com.service.transfer.exceptions.TransferException;
import feign.codec.DecodeException;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@RestControllerAdvice
public class TransferExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<?>> handleGeneralException(RuntimeException e) {
        ResponseWrapper<?> responseWrapper = ResponseWrapper.ofError(new ErrorDto(ErrorEnum.GENERAL_ERROR));
        return new ResponseEntity<>(responseWrapper, responseWrapper.getStatus());
    }

}
