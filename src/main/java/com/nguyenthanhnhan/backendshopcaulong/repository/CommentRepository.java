package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByProductProductId(UUID productId);
    long countByProductProductId(UUID productId);
}

