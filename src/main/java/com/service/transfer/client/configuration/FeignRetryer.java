package com.service.transfer.client.configuration;

import com.service.transfer.exceptions.ClientException;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.http.HttpStatus;

public class FeignRetryer implements Retryer {
    private final int maxAttempts = 3;
    private final long period = 5000L;
    private final long maxPeriod = 10000L;
    private int attempt = 1;

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "Retry limit exceeded: " + e.getMessage());
        }
        try {
            Thread.sleep(Math.min(period * attempt, maxPeriod));
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR, "Retry interrupted: "+e.getMessage());
        }
    }

    @Override
    public Retryer clone() {
        return new FeignRetryer();
    }
}
