package com.service.transfer.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferByAccountNumberRequest {

    @NotBlank(message = "fromAccountNumber is required")
    private String fromAccountNumber;
    @NotBlank(message = "toAccountNumber is required")
    private String toAccountNumber;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.001", message = "Transfer amount must be greater than 0")
    private BigDecimal amount;
}
