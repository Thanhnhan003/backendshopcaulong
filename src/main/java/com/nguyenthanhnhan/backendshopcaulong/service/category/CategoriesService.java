package com.nguyenthanhnhan.backendshopcaulong.service.category;

import com.nguyenthanhnhan.backendshopcaulong.entity.Categories;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.repository.CategoriesRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class CategoriesService {
    private static final String UPLOAD_DIR = "src/main/resources/static/dataImage/categorys/";

    @Autowired
    private CategoriesRepository categoriesRepository;



    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Optional<Categories> getCategoryById(UUID id) {
        return categoriesRepository.findById(id);
    }

    public Categories saveCategory(Categories category) {
        if (categoriesRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("Tên của category đã tồn tại");
        }
        category.setSlug(generateSlug(category.getCategoryName()));
        return categoriesRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoriesRepository.deleteById(id);
    }

    public Categories findById(UUID categoryId) {
        Optional<Categories> category = categoriesRepository.findById(categoryId);
        return category.orElse(null);
    }

    public Optional<Categories> findBySlug(String slug) {
        return categoriesRepository.findBySlug(slug);
    }

    private String generateSlug(String categoryName) {
        String normalized = Normalizer.normalize(categoryName, Normalizer.Form.NFD);
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
