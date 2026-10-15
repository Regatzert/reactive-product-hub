package com.reactivehub.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

class ExternalProductServiceTest {

    private MockWebServer mockWebServer;
    private ExternalProductService externalProductService;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        externalProductService = new ExternalProductService(
                webClient,
                Duration.ofSeconds(2));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnProductWhenProviderRespondsSuccessfully()
            throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "id": 1,
                          "title": "Producto de prueba",
                          "description": "Descripción",
                          "price": 25.50,
                          "discountPercentage": 5.0,
                          "rating": 4.5,
                          "brand": "Demo",
                          "category": "test"
                        }
                        """));

        StepVerifier.create(externalProductService.findById(1L))
                .assertNext(product -> {
                    assertEquals(1L, product.id());
                    assertEquals("Producto de prueba", product.title());
                })
                .verifyComplete();

        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    void shouldRetryWhenProviderRespondsWithServerError()
            throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "id": 1,
                          "title": "Producto recuperado",
                          "description": "Descripción",
                          "price": 25.50,
                          "discountPercentage": 0.0,
                          "rating": 4.0,
                          "brand": "Demo",
                          "category": "test"
                        }
                        """));

        StepVerifier.create(externalProductService.findById(1L))
                .assertNext(product ->
                        assertEquals("Producto recuperado", product.title()))
                .verifyComplete();

        assertEquals(2, mockWebServer.getRequestCount());
    }

    @Test
    void shouldNotRetryWhenProviderRespondsWithNotFound()
            throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(externalProductService.findById(999L))
                .expectError()
                .verify();

        assertEquals(1, mockWebServer.getRequestCount());
    }
}