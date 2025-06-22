package com.service.transfer.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferByIbanRequest {
    @NotBlank(message = "fromIban is required")
    private String fromIban;
    @NotBlank(message = "toIban is required")
    private String toIban;
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than 0")
    private BigDecimal amount;
}
