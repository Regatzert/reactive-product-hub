package com.reactivehub.product.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.reactivehub.product.model.Product;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long>{
    
}
