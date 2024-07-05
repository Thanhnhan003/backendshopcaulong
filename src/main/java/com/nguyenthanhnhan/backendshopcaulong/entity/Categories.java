package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "categoryId")
    private UUID categoryId;

    @Column(name = "categoryName")
    private String categoryName;

    @Column(name = "categoryDescription")
    private String categoryDescription;
    
    @Column(name = "slug")
    private String slug;

    @Column(name = "thumbnail")
    private String thumbnail;
}
