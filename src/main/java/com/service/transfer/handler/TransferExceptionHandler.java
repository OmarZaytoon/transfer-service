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

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ResponseWrapper<?>> handleTransferException(TransferException e) {
        ResponseWrapper<?> responseWrapper = ResponseWrapper.ofError(e.getErrorDto());
        return new ResponseEntity<>(responseWrapper, responseWrapper.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<?>> handleGeneralException(RuntimeException e) {
        ResponseWrapper<?> responseWrapper = ResponseWrapper.ofError(new ErrorDto(ErrorEnum.GENERAL_ERROR));
        return new ResponseEntity<>(responseWrapper, responseWrapper.getStatus());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        ResponseWrapper<?> responseWrapper = new ResponseWrapper<>(HttpStatus.BAD_REQUEST);
        ex.getBindingResult().getFieldErrors().forEach(error ->
                responseWrapper.addError(new ErrorDto(error.getField(), error.getDefaultMessage(), HttpStatus.BAD_REQUEST)));

        return ResponseEntity.badRequest().body(responseWrapper);
    }

    @ExceptionHandler(DecodeException.class)
    public ResponseEntity<?> handleFigenDecoderException(DecodeException ex) {
        if (ex.getCause() instanceof ClientException) {
            ResponseWrapper<?> responseWrapper = new ResponseWrapper<>(((ClientException) ex.getCause()).getStatus());
            responseWrapper.addError(((ClientException) ex.getCause()).getErrorDto());
            return new ResponseEntity<>(responseWrapper, responseWrapper.getStatus());
        } else {
            ResponseWrapper<?> responseWrapper = new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR);
            responseWrapper.addError("UNDEFINED", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
