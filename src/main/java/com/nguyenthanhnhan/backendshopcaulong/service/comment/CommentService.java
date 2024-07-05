package com.nguyenthanhnhan.backendshopcaulong.service.comment;

import com.nguyenthanhnhan.backendshopcaulong.entity.Comment;
import com.nguyenthanhnhan.backendshopcaulong.repository.CommentRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public List<Comment> getCommentsByProductId(UUID productId) {
        return commentRepository.findByProductProductId(productId);
    }

     public Map<Integer, Long> getRatingStatisticsByProductId(UUID productId) {
        List<Comment> comments = commentRepository.findByProductProductId(productId);
        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getRating, Collectors.counting()));
    }

    public double getAverageRatingByProductId(UUID productId) {
        List<Comment> comments = commentRepository.findByProductProductId(productId);
        return comments.stream()
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);
    }

    public long getTotalReviewsByProductId(UUID productId) {
        return commentRepository.countByProductProductId(productId);
    }
}