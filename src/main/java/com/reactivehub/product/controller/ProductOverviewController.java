package com.reactivehub.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reactivehub.product.dto.ProductOverviewResponse;
import com.reactivehub.product.service.ProductOverviewService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ProductOverviewController {

    private final ProductOverviewService productOverviewService;

    public ProductOverviewController(
            ProductOverviewService productOverviewService) {
        this.productOverviewService = productOverviewService;
    }

    @GetMapping("/{localId}/overview")
    public Mono<ResponseEntity<ProductOverviewResponse>> findOverview(
            @PathVariable Long localId,
            @RequestParam Long externalId) {

        if (localId <= 0 || externalId <= 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return productOverviewService
                .findOverview(localId, externalId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
