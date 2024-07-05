package com.nguyenthanhnhan.backendshopcaulong.service.news;


import com.nguyenthanhnhan.backendshopcaulong.entity.News;
import com.nguyenthanhnhan.backendshopcaulong.repository.NewsRepository;

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
public class NewsService {
   private static final String UPLOAD_DIR = "src/main/resources/static/dataImage/news/";
    @Autowired
    private NewsRepository newsRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public Optional<News> getNewsById(UUID id) {
        return newsRepository.findById(id);
    }

    public News saveNews(News news) {
        if (newsRepository.existsByTitle(news.getTitle())) {
            throw new IllegalArgumentException("Tên của news đã tồn tại");
        }
        news.setSlug(generateSlug(news.getTitle()));
        return newsRepository.save(news);
    }
    public News updatNews(News news) {
        news.setSlug(generateSlug(news.getTitle()));
        return newsRepository.save(news);
    }
    public void deleteNews(UUID id) {
        newsRepository.deleteById(id);
    }

    public News findById(UUID newsId) {
        Optional<News> news = newsRepository.findById(newsId);
        return news.orElse(null);
    }

    public Optional<News> findBySlug(String slug) {
        return newsRepository.findBySlug(slug);
    }

    private String generateSlug(String newsName) {
        String normalized = Normalizer.normalize(newsName, Normalizer.Form.NFD);
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
