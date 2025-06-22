package com.service.transfer.client.impl;

import com.service.transfer.client.ExchangeRateClient;
import com.service.transfer.client.impl.figen.ExchangeRateAPIClient;
import com.service.transfer.dto.responses.ExchangeRateResponse;
import com.service.transfer.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExchangeRateClientImpl implements ExchangeRateClient {
    private final static Logger log = LoggerFactory.getLogger(ExchangeRateClientImpl.class);
    private final ExchangeRateAPIClient exchangeRateAPIClient;

    @Override
    public BigDecimal getExchangeRate(String from, String to) {
        log.info("Calling exchange rate API for base currency: [{}]", from);
        ExchangeRateResponse response = exchangeRateAPIClient.getLatestExchangeRates(from);
        if (Objects.isNull(response) || Objects.isNull(response.getRates()) || !response.getRates().containsKey(to)) {
            throw new ClientException(HttpStatus.BAD_REQUEST);
        }
        log.info("Received valid exchange rate response for base [{}]", response.getBase());
        return response.getRates().get(to);

    }
}
