package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriesRepository extends JpaRepository<Categories, UUID> {
    boolean existsByCategoryName(String categoryName);
    Optional<Categories> findBySlug(String slug);
}
