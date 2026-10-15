package com.reactivehub.product.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivehub.product.controller.ProductController;
import com.reactivehub.product.model.Product;


import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldRejectProductWithNegativePrice() {
        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Producto inválido",
                          "description": "Prueba",
                          "price": -10,
                          "stock": 5
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(productService);
    }

    @Test
    void shouldRejectProductWithoutName() {
        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": " ",
                          "description": "Prueba",
                          "price": 10,
                          "stock": 5
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(productService);
    }

    @Test
    void shouldCreateProductWhenRequestIsValid() {
        Product product = new Product(
                1L,
                "Teclado",
                "Teclado mecánico",
                new BigDecimal("149.90"),
                5,
                null);

        when(productService.create(any()))
                .thenReturn(Mono.just(product));

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Teclado",
                          "description": "Teclado mecánico",
                          "price": 149.90,
                          "stock": 5
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Teclado");

        verify(productService).create(any());
    }
}
