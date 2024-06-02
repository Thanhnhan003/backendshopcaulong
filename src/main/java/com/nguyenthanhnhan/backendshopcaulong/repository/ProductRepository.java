package com.nguyenthanhnhan.backendshopcaulong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findTop8ByOrderByCreatedAtDesc();

    boolean existsByProductName(String productName);

    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);
}
