package com.reactivehub.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.reactivehub.product.dto.ExternalProductResponse;
import com.reactivehub.product.service.ExternalProductService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/external-products")
public class ExternalProductController {

    private final ExternalProductService externalProductService;

    public ExternalProductController(
            ExternalProductService externalProductService) {
        this.externalProductService = externalProductService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ExternalProductResponse>> findById(
            @PathVariable Long id) {

        if (id <= 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return externalProductService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(
                        Exception.class,
                        ex -> Mono.just(
                                ResponseEntity.status(502).build()));
    }
}