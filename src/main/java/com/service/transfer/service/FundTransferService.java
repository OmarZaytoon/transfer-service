package com.service.transfer.service;

import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.entity.transactions.TransactionInfo;

import java.math.BigDecimal;

public interface FundTransferService {
    ResponseWrapper<TransferResponse> transferFundsByAccountNumber(TransferByAccountNumberRequest request);


    ResponseWrapper<TransferResponse> transferFundsByIban(TransferByIbanRequest request);
}
