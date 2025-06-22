package com.service.transfer.controller.impl;


import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.service.FundTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfer")
public class TransferControllerImpl {

    private final FundTransferService fundTransferService;

    @PostMapping("/by-account-number")
    public ResponseEntity<ResponseWrapper<TransferResponse>> transferByAccountNumber(@Valid @RequestBody TransferByAccountNumberRequest request) {
        return ResponseEntity.ok(
                fundTransferService.transferFundsByAccountNumber(request)
        );
    }

    @PostMapping("/by-iban-number")
    public ResponseEntity<ResponseWrapper<TransferResponse>> transferByIban(@Valid @RequestBody TransferByIbanRequest request) {
        return ResponseEntity.ok(
                fundTransferService.transferFundsByIban(request)
        );
    }
}

