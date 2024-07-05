package com.nguyenthanhnhan.backendshopcaulong.service.product;

import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    // Lọc theo giá
    public static Specification<Product> hasPriceBetween(String minPrice, String maxPrice) {
        return (root, query, criteriaBuilder) -> {
            BigDecimal min = new BigDecimal(minPrice);
            BigDecimal max = new BigDecimal(maxPrice);
            return criteriaBuilder.between(root.get("productPrice").as(BigDecimal.class), min, max);
        };
    }

    // Lọc theo slug category
    public static Specification<Product> hasCategorySlug(String categorySlug) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("category").get("slug"), categorySlug);
    }

    // Lọc theo slug brand
    public static Specification<Product> hasBrandSlug(String brandSlug) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("brand").get("slug"), brandSlug);
    }
}