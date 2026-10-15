package com.reactivehub.product.dto;

import com.reactivehub.product.model.Product;

public record ProductOverviewResponse(
    Product localProduct,
        ExternalProductResponse externalProduct,
        String externalStatus
) {
    
}
