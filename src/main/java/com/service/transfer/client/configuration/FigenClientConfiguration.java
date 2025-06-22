package com.service.transfer.client.configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FigenClientConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FigenClientErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new FeignRetryer();
    }
    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(
                40,
                TimeUnit.SECONDS,
                30,
                TimeUnit.SECONDS,
                true
        );
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder feignDecoder() {
        return new FeignClientDecoder();
    }
}
