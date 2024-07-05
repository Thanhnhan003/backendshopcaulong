package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findTop10ByOrderByCreatedAtDesc();

    boolean existsByProductName(String productName);

    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);
    //list sản phẩm dựa vào slug category
    List<Product> findByCategorySlug(String categorySlug);
    List<Product> findByCategorySlugAndBrandSlug(String categorySlug, String brandSlug);
    /////====================
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    List<Product> findByCategoryCategoryIdAndBrandBrandId(UUID categoryId, UUID brandId);

    @Query("SELECT p FROM Product p WHERE p.quantity < 10 ORDER BY p.quantity ASC")
    List<Product> findProductsWithLowQuantity();
}
