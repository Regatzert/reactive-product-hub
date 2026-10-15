package com.reactivehub.product.service;

import org.springframework.stereotype.Service;

import com.reactivehub.product.dto.ProductRequest;
import com.reactivehub.product.model.Product;
import com.reactivehub.product.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return productRepository.save(product);
    }

    public Mono<Product> update(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(request.name());
                    existing.setDescription(request.description());
                    existing.setPrice(request.price());
                    existing.setStock(request.stock());

                    return productRepository.save(existing);
                });
    }

    public Mono<Boolean> delete(Long id) {
        return productRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(false);
                    }

                    return productRepository.deleteById(id)
                            .thenReturn(true);
                });
    }
}
