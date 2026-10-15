package com.reactivehub.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reactivehub.product.dto.ExternalProductResponse;
import com.reactivehub.product.model.Product;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductOverviewServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ExternalProductService externalProductService;

    @InjectMocks
    private ProductOverviewService productOverviewService;

    private Product localProduct;

    @BeforeEach
    void setUp() {
        localProduct = new Product(
                1L,
                "Teclado mecánico",
                "Teclado para desarrollo",
                new BigDecimal("149.90"),
                10,
                LocalDateTime.now());
    }

    @Test
    void shouldReturnOverviewWhenBothSourcesSucceed() {
        ExternalProductResponse externalProduct =
                new ExternalProductResponse(
                        1L,
                        "External keyboard",
                        "External description",
                        new BigDecimal("99.90"),
                        new BigDecimal("5.00"),
                        new BigDecimal("4.50"),
                        "Example brand",
                        "electronics");

        when(productService.findById(1L))
                .thenReturn(Mono.just(localProduct));

        when(externalProductService.findById(10L))
                .thenReturn(Mono.just(externalProduct));

        StepVerifier.create(
                        productOverviewService.findOverview(1L, 10L))
                .assertNext(result -> {
                    assertEquals(localProduct, result.localProduct());
                    assertEquals(externalProduct, result.externalProduct());
                    assertEquals("AVAILABLE", result.externalStatus());
                })
                .verifyComplete();

        verify(productService).findById(1L);
        verify(externalProductService).findById(10L);
    }

    @Test
    void shouldReturnLocalProductWhenExternalServiceFails() {
        when(productService.findById(1L))
                .thenReturn(Mono.just(localProduct));

        when(externalProductService.findById(10L))
                .thenReturn(Mono.error(
                        new RuntimeException("External service unavailable")));

        StepVerifier.create(
                        productOverviewService.findOverview(1L, 10L))
                .assertNext(result -> {
                    assertEquals(localProduct, result.localProduct());
                    assertNull(result.externalProduct());
                    assertEquals("UNAVAILABLE", result.externalStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenLocalProductDoesNotExist() {
        when(productService.findById(999L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        productOverviewService.findOverview(999L, 10L))
                .verifyComplete();

        verifyNoInteractions(externalProductService);
    }
}