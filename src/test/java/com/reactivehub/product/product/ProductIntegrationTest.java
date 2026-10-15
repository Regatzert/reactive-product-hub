package com.reactivehub.product.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import com.reactivehub.product.dto.ProductRequest;
import com.reactivehub.product.model.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient client() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldCreateAndRetrieveProduct() {
        ProductRequest request = new ProductRequest(
                "Producto de integracion",
                "Producto creado durante una prueba",
                new BigDecimal("29.90"),
                10
        );

        Product created = client()
                .post()
                .uri("/api/v1/products")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName())
                .isEqualTo("Producto de integracion");

        client()
                .get()
                .uri("/api/v1/products/{id}", created.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Producto de integracion");
    }
}
