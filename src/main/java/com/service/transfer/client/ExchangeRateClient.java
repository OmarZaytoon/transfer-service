package com.service.transfer.client;

import java.math.BigDecimal;

public interface ExchangeRateClient {
    BigDecimal getExchangeRate(String from, String to);
}
