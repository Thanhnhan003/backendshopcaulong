package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productId")
    private UUID productId;

    @Column(name = "productName")
    private String productName;

    @Column(name = "slug")
    private String slug;

    @Column(name = "productPrice")
    private String productPrice;

    @Lob
    @Column(name = "productDescription", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "quantity")
    private int quantity; // Số lượng

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private Categories category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brandId", referencedColumnName = "brandId")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gallery> galleries;   
}
