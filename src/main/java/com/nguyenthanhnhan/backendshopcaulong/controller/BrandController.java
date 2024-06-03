package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.service.brand.BrandService;

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
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/show")
    public List<Brand> getAllBrand() {
        return brandService.getAllBrand();
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable UUID id) {
        Optional<Brand> brand = brandService.getBrandById(id);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Brand> getBrandBySlug(@PathVariable String slug) {
        Optional<Brand> brand = brandService.findBySlug(slug);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@RequestParam("brandName") String brandName,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String imageName = brandService.saveImage(imageFile);
            Brand brand = new Brand();
            brand.setBrandName(brandName);
            brand.setThumbnail(imageName);
            return ResponseEntity.ok(brandService.saveBrand(brand));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable UUID id,
            @RequestParam("brandName") String brandName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Optional<Brand> brandOptional = brandService.getBrandById(id);
        if (brandOptional.isPresent()) {
            try {
                Brand brand = brandOptional.get();
                brand.setBrandName(brandName);
                if (imageFile != null && !imageFile.isEmpty()) {
                    String oldImage = brand.getThumbnail();
                    String imageName = brandService.saveImage(imageFile);
                    brand.setThumbnail(imageName);
                    if (oldImage != null) {
                        brandService.deleteImage(oldImage);
                    }
                }
                return ResponseEntity.ok(brandService.saveBrand(brand));
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
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        Optional<Brand> brandOptional = brandService.getBrandById(id);
        if (brandOptional.isPresent()) {
            Brand brand = brandOptional.get();
            brandService.deleteBrand(id);
            try {
                brandService.deleteImage(brand.getThumbnail());
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

        Resource resource = new ClassPathResource("static/dataImage/brands/" + thumbnail);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate image media type

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
