package com.nguyenthanhnhan.backendshopcaulong.service.brand;


import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.repository.BrandRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class BrandService {
   private static final String UPLOAD_DIR = "src/main/resources/static/dataImage/brands/";
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(UUID id) {
        return brandRepository.findById(id);
    }

    public Brand saveBrand(Brand brand) {
        if (brandRepository.existsByBrandName(brand.getBrandName())) {
            throw new IllegalArgumentException("Tên của brand đã tồn tại");
        }
        brand.setSlug(generateSlug(brand.getBrandName()));
        return brandRepository.save(brand);
    }

    public void deleteBrand(UUID id) {
        brandRepository.deleteById(id);
    }

    public Brand findById(UUID brandId) {
        Optional<Brand> brand = brandRepository.findById(brandId);
        return brand.orElse(null);
    }

    public Optional<Brand> findBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }

    private String generateSlug(String brandName) {
        String normalized = Normalizer.normalize(brandName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("").toLowerCase();
        return slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }

    public String saveImage(MultipartFile imageFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        String imagePath = UPLOAD_DIR + imageName;
        Files.copy(imageFile.getInputStream(), Paths.get(imagePath));
        return imageName;
    }

    public void deleteImage(String imageName) throws IOException {
        Files.deleteIfExists(Paths.get(UPLOAD_DIR + imageName));
    }
}
