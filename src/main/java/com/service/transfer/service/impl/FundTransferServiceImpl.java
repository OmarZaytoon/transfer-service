package com.service.transfer.service.impl;

import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.service.FundTransferService;
import org.springframework.stereotype.Service;

@Service
public class FundTransferServiceImpl implements FundTransferService {
    @Override
    public ResponseWrapper<TransferResponse> transferFundsByAccountNumber(TransferByAccountNumberRequest request) {
        return null;
    }

    @Override
    public ResponseWrapper<TransferResponse> transferFundsByIban(TransferByIbanRequest request) {
        return null;
    }
}
