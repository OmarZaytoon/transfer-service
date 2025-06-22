package com.service.transfer.client.configuration;

import com.service.transfer.exceptions.ClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FigenClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status.is4xxClientError() || status.is5xxServerError()) {
            return new ClientException(status);
        }else{
            return new ClientException(HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
}
