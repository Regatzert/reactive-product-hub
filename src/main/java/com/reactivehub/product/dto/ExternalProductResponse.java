package com.reactivehub.product.dto;

import java.math.BigDecimal;

public record ExternalProductResponse(
    Long id,
        String title,
        String description,
        BigDecimal price,
        BigDecimal discountPercentage,
        BigDecimal rating,
        String brand,
        String category
) {
    
}
