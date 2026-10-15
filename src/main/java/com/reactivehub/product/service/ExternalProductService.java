package com.reactivehub.product.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import com.reactivehub.product.dto.ExternalProductResponse;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ExternalProductService {

    private final WebClient externalProductsWebClient;
    private final Duration timeout;

    public ExternalProductService(
            WebClient externalProductsWebClient,
            @Value("${external.products.timeout}") Duration timeout) {
        this.externalProductsWebClient = externalProductsWebClient;
        this.timeout = timeout;
    }

    public Mono<ExternalProductResponse> findById(Long id) {
        return externalProductsWebClient
                .get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(ExternalProductResponse.class)
                .timeout(timeout)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(200))
                                .filter(this::isRetryable)
                                .onRetryExhaustedThrow(
                                        (spec, signal) -> signal.failure()));
    }

    private boolean isRetryable(Throwable error) {
        if (error instanceof WebClientRequestException
                || error instanceof TimeoutException) {
            return true;
        }

        if (error instanceof WebClientResponseException responseException) {
            return responseException.getStatusCode().is5xxServerError();
        }

        return false;
    }
}