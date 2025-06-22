package com.service.transfer.dto.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String base;
    private Map<String, BigDecimal> rates;

    public String getBase() {
        return base;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
