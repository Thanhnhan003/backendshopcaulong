package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.Comment;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.service.comment.CommentService;
import com.nguyenthanhnhan.backendshopcaulong.service.product.ProductService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.UserInfoService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.UserInfoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getCommentsByProductId(@PathVariable UUID productId) {
        List<Comment> comments = commentService.getCommentsByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token,
            @RequestParam UUID productId,
            @RequestParam String commentText,
            @RequestParam int rating) {
        // Kiểm tra token và lấy thông tin userId
        String jwtToken = token.substring(7);
        UUID userId = jwtService.extractUserId(jwtToken);

        // Lấy thông tin người dùng và sản phẩm từ database
        Users user = userInfoService.findUserById(userId);
        Product product = productService.getProductById(productId);

        // Tạo và lưu bình luận mới
        Comment newComment = new Comment();
        newComment.setCommentText(commentText);
        newComment.setRating(rating);
        newComment.setUser(user);
        newComment.setProduct(product);
        commentService.saveComment(newComment);

        return ResponseEntity.ok("Comment added successfully");
    }

    @GetMapping("/statistics/{productId}")
    public ResponseEntity<?> getCommentStatisticsByProductId(@PathVariable UUID productId) {
        Map<Integer, Long> ratingStatistics = commentService.getRatingStatisticsByProductId(productId);
        double averageRating = commentService.getAverageRatingByProductId(productId);
        long totalReviews = commentService.getTotalReviewsByProductId(productId);

        return ResponseEntity.ok(Map.of(
                "ratingStatistics", ratingStatistics,
                "averageRating", averageRating,
                "totalReviews", totalReviews
        ));
    }
}
