package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "commentId")
    private UUID commentId;

    @Lob
    @Column(name = "commentText", columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "rating")
    private int rating; // Đánh giá sản phẩm

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;
}
