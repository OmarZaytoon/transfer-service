package com.service.transfer.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.service.transfer.exceptions.ClientException;
import feign.codec.DecodeException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.math.BigDecimal;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExchangeRateClientTest {

    private static final int WIREMOCK_PORT = 8089;
    private WireMockServer wireMockServer;

    @Autowired
    private ExchangeRateClient client;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("exchange.api.base-url", () -> "http://localhost:" + WIREMOCK_PORT);
    }

    @BeforeAll
    void startWireMockServer() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
    }

    @AfterAll
    void stopWireMockServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void resetStubs() {
        wireMockServer.resetAll();
    }

    @Test
    void testGetExchangeRate200() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "base": "USD",
                      "rates": {
                        "EUR": 0.85
                      }
                    }
                """)));

        BigDecimal exchangeRate = client.getExchangeRate("USD","EUR");

        assertNotNull(exchangeRate);
        assertEquals(new BigDecimal("0.85"), exchangeRate);
    }
    @Test
    void exchangeClientReturnsHttpEmpty() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "base": "USD",
                      "rates": {
                        "EUR": 0.85
                      }
                    }
                """)));

        ClientException ex = assertThrows(ClientException.class, () -> client.getExchangeRate("USD","TST"));
        assertEquals(400, ex.getStatus().value());

    }
    @Test
    void exchangeClientReturnsHttp400() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse().withStatus(400)));
        ClientException ex = assertThrows(ClientException.class, () -> client.getExchangeRate("USD","EUR"));
        assertEquals(400, ex.getStatus().value());
    }



    @Test
    void exchangeClientReturnsHttp504() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse().withStatus(504)));
        ClientException ex = assertThrows(ClientException.class, () -> client.getExchangeRate("USD","EUR"));
        assertEquals(504, ex.getStatus().value());
    }


    @Test
    void exchangeClientReturnsTimeout() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse()
                        .withFixedDelay(50_000)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "base": "USD",
                      "rates": {
                        "EUR": 0.85
                      }
                    }
                """)));
        ClientException ex = assertThrows(ClientException.class, () -> client.getExchangeRate("USD","EUR"));
        assertEquals(503, ex.getStatus().value());

    }

    @Test
    void testSerializationError() {
        wireMockServer.stubFor(get(urlEqualTo("/latest/USD"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "base": "USD",
                          "rates": {
                            "EUR": "eighty-five"
                          }
                        }
                    """)));

        DecodeException ex = assertThrows(DecodeException.class, () -> client.getExchangeRate("USD", "EUR"));

        assertNotNull(ex.getCause());
        assertInstanceOf(ClientException.class, ex.getCause());
        ClientException clientEx = (ClientException) ex.getCause();
        assertEquals(500, clientEx.getStatus().value());
    }

    @Test
    void testNetworkDown() {
        wireMockServer.stop();
        ClientException ex = assertThrows(ClientException.class, () -> client.getExchangeRate("USD","EUR"));
        assertEquals(503, ex.getStatus().value());
        wireMockServer.start();
    }
}
