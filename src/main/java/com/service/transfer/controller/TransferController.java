package com.service.transfer.controller;

import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import org.springframework.http.ResponseEntity;

public interface TransferController {
    ResponseEntity<ResponseWrapper<TransferResponse>> transferByAccountNumber(TransferByAccountNumberRequest request);
    ResponseEntity<ResponseWrapper<TransferResponse>> transferByIban(TransferByIbanRequest request);
}
