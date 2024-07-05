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
@Table(name = "banners")
public class Banners {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bannerId")
    private UUID bannerId;

    @Column(name = "bannerName")
    private String bannerName;

    @Column(name = "bannerDescription")
    private String bannerDescription;

    @Column(name = "urlImg")
    private String urlImg;

    @Column(name = "urlLink")
    private String urlLink;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Thêm hàm tạo này
    public Banners(UUID bannerId, String bannerName, String bannerDescription, String urlImg, String urlLink,
            boolean enabled) {
        this.bannerId = bannerId;
        this.bannerName = bannerName;
        this.bannerDescription = bannerDescription;
        this.urlImg = urlImg;
        this.urlLink = urlLink;
        this.enabled = enabled;
        this.createdAt = LocalDateTime.now(); // Set createdAt to current time
    }
}
