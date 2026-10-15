package com.reactivehub.product.service;

import org.springframework.stereotype.Service;

import com.reactivehub.product.dto.ProductOverviewResponse;

import reactor.core.publisher.Mono;

@Service
public class ProductOverviewService {

    private final ProductService productService;
    private final ExternalProductService externalProductService;

    public ProductOverviewService(
            ProductService productService,
            ExternalProductService externalProductService) {
        this.productService = productService;
        this.externalProductService = externalProductService;
    }

    public Mono<ProductOverviewResponse> findOverview(
            Long localId,
            Long externalId) {

        return productService.findById(localId)
                .flatMap(localProduct ->
                        externalProductService.findById(externalId)
                                .map(externalProduct ->
                                        new ProductOverviewResponse(
                                                localProduct,
                                                externalProduct,
                                                "AVAILABLE"))
                                .onErrorResume(error ->
                                        Mono.just(
                                                new ProductOverviewResponse(
                                                        localProduct,
                                                        null,
                                                        "UNAVAILABLE"))));
    }
}
