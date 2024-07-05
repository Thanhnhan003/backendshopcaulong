package com.nguyenthanhnhan.backendshopcaulong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.News;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    boolean existsByTitle(String title);

    Optional<News> findBySlug(String slug);
}
