package com.service.transfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.transfer.controller.impl.TransferControllerImpl;
import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.service.FundTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransferControllerImpl.class)
public class TransferControllerTest {
    private final static String transactionId= System.currentTimeMillis()+"-"+UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundTransferService fundTransferService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransferByAccountNumberRequest validAccountNumberRequest;
    private TransferByIbanRequest validIbanRequest;

    @BeforeEach
    void setUp() {
        validAccountNumberRequest = new TransferByAccountNumberRequest();
        validAccountNumberRequest.setFromAccountNumber("131000301");
        validAccountNumberRequest.setToAccountNumber("131000302");
        validAccountNumberRequest.setAmount(BigDecimal.valueOf(100));

        validIbanRequest = new TransferByIbanRequest();
        validIbanRequest.setFromIban("JO94CBJO0010000000000131000301");
        validIbanRequest.setToIban("JO94CBJO0010000000000131000302");
        validIbanRequest.setAmount(BigDecimal.valueOf(50));
    }

    @Test
    void successTransferByAccountNumber() throws Exception {
        TransferResponse response = new TransferResponse(transactionId);
        ResponseWrapper<TransferResponse> wrapper = ResponseWrapper.of(response);

        Mockito.when(fundTransferService.transferFundsByAccountNumber(any()))
                .thenReturn(wrapper);

        mockMvc.perform(post("/api/v1/transfer/by-account-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAccountNumberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.transactionId").value(transactionId));
    }
    @Test
    void successTransferByIbanNumber() throws Exception {
        TransferResponse response = new TransferResponse(transactionId);
        ResponseWrapper<TransferResponse> wrapper = ResponseWrapper.of(response);

        Mockito.when(fundTransferService.transferFundsByIban(any()))
                .thenReturn(wrapper);

        mockMvc.perform(post("/api/v1/transfer/by-iban-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIbanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.transactionId").value(transactionId));
    }
    @Test
    void transferByAccountNumberNullFields() throws Exception {
        TransferByAccountNumberRequest invalidRequest = new TransferByAccountNumberRequest();

        mockMvc.perform(post("/api/v1/transfer/by-account-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message[0].errorCode").exists())
                .andExpect(jsonPath("$.message[0].errorMessage").exists());
    }
    @Test
    void transferByIbanNumberNullFields() throws Exception {
        TransferByIbanRequest invalidRequest = new TransferByIbanRequest();

        mockMvc.perform(post("/api/v1/transfer/by-iban-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message[0].errorCode").exists())
                .andExpect(jsonPath("$.message[0].errorMessage").exists());
    }
    @Test
    void transferByIbanInvalidAmount() throws Exception {
        TransferByIbanRequest invalidAmountRequest = new TransferByIbanRequest();
        invalidAmountRequest.setFromIban("JO94CBJO0010000000000131000301");
        invalidAmountRequest.setToIban("JO94CBJO0010000000000131000302");
        invalidAmountRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/transfer/by-iban-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAmountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message[0].errorCode").exists())
                .andExpect(jsonPath("$.message[0].errorMessage").exists());
    }

    @Test
    void transferByAccountNumberInvalidAmount() throws Exception {
        TransferByAccountNumberRequest negativeAmountRequest = new TransferByAccountNumberRequest();
        negativeAmountRequest.setFromAccountNumber("131000301");
        negativeAmountRequest.setToAccountNumber("131000302");
        negativeAmountRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/transfer/by-account-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeAmountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message[0].errorCode").exists())
                .andExpect(jsonPath("$.message[0].errorMessage").exists());
    }

}
