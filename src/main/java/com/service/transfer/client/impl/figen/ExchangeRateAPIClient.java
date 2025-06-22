package com.service.transfer.client.impl.figen;

import com.service.transfer.dto.responses.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchangeRateAPIClient", url = "${exchange.api.base-url}")
public interface ExchangeRateAPIClient {
    @GetMapping("/latest/{from}")
    ExchangeRateResponse getLatestExchangeRates(@PathVariable("from") String from);
}
