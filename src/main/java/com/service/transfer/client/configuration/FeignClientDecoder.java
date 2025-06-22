package com.service.transfer.client.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.service.transfer.exceptions.ClientException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.http.HttpStatus;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FeignClientDecoder implements Decoder {

    private final ObjectMapper objectMapper=newMapper();


    @Override
    public Object decode(Response response, Type type) {
        if (Objects.isNull(response) || Objects.isNull(response.body())) {
            return null;
        }

        try (Reader reader = new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8)) {
            return objectMapper.readValue(reader, objectMapper.constructType(type));
        } catch (JsonProcessingException e) {
            throw new DecodeException(
                    response.status(),
                    "Unexpected error while decoding response",
                    response.request(),
                    new ClientException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed To Read Response Json")
            );
        } catch (Exception e) {
            throw new DecodeException(
                    response.status(),
                    "Unexpected error while decoding response",
                    response.request(),
                    new ClientException(HttpStatus.INTERNAL_SERVER_ERROR)
            );

        }
    }
    private static ObjectMapper newMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }
}
