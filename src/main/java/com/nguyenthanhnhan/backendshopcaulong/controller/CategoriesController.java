package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.Categories;
import com.nguyenthanhnhan.backendshopcaulong.service.category.CategoriesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/category")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/show")
    public List<Categories> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable UUID id) {
        Optional<Categories> category = categoriesService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Categories> getCategoryBySlug(@PathVariable String slug) {
        Optional<Categories> category = categoriesService.findBySlug(slug);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestParam("categoryName") String categoryName,
            @RequestParam("categoryDescription") String categoryDescription,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String imageName = categoriesService.saveImage(imageFile);
            Categories category = new Categories();
            category.setCategoryName(categoryName);
            category.setCategoryDescription(categoryDescription);
            category.setThumbnail(imageName);
            return ResponseEntity.ok(categoriesService.saveCategory(category));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("categoryDescription") String categoryDescription,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Optional<Categories> categoryOptional = categoriesService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            try {
                Categories category = categoryOptional.get();
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDescription);
                if (imageFile != null && !imageFile.isEmpty()) {
                    String oldImage = category.getThumbnail();
                    String imageName = categoriesService.saveImage(imageFile);
                    category.setThumbnail(imageName);
                    if (oldImage != null) {
                        categoriesService.deleteImage(oldImage);
                    }
                }
                return ResponseEntity.ok(categoriesService.saveCategory(category));
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error updating image: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        Optional<Categories> categoryOptional = categoriesService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            Categories category = categoryOptional.get();
            categoriesService.deleteCategory(id);
            try {
                categoriesService.deleteImage(category.getThumbnail());
            } catch (IOException e) {
                // Handle image delete exception
            }
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/images/{thumbnail}")
    public ResponseEntity<byte[]> getImage(@PathVariable String thumbnail) throws IOException {
        // You should sanitize and validate the thumbnail parameter to prevent directory
        // traversal attacks.
        // In this example, it is assumed that thumbnail is just the name of the image
        // file.

        Resource resource = new ClassPathResource("static/dataImage/categorys/" + thumbnail);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate image media type

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
    
}
