package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.News;
import com.nguyenthanhnhan.backendshopcaulong.service.news.NewsService;

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
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/show")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable UUID id) {
        Optional<News> news = newsService.getNewsById(id);
        if (news.isPresent()) {
            return ResponseEntity.ok(news.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<News> getNewsBySlug(@PathVariable String slug) {
        Optional<News> news = newsService.findBySlug(slug);
        if (news.isPresent()) {
            return ResponseEntity.ok(news.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createNews(@RequestParam("newsName") String newsName,
            @RequestParam("content") String content,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String imageName = newsService.saveImage(imageFile);
            News news = new News();
            news.setTitle(newsName);
            news.setContent(content);
            news.setThumbnail(imageName);
            return ResponseEntity.ok(newsService.saveNews(news));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable UUID id,
            @RequestParam("newsName") String newsName,
            @RequestParam("content") String content,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Optional<News> newsOptional = newsService.getNewsById(id);
        if (newsOptional.isPresent()) {
            try {
                News news = newsOptional.get();
                boolean isUpdated = false;

                if (!news.getTitle().equals(newsName)) {
                    news.setTitle(newsName);
                    isUpdated = true;
                }

                if (!news.getContent().equals(content)) {
                    news.setContent(content);
                    isUpdated = true;
                }

                if (imageFile != null && !imageFile.isEmpty()) {
                    String oldImage = news.getThumbnail();
                    String imageName = newsService.saveImage(imageFile);
                    news.setThumbnail(imageName);
                    if (oldImage != null) {
                        newsService.deleteImage(oldImage);
                    }
                    isUpdated = true;
                }

                if (isUpdated) {
                    return ResponseEntity.ok(newsService.updatNews(news));
                } else {
                    return ResponseEntity.status(304).body("Bạn chưa thay đổi thông tin gì thương hiệu.");
                }
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
    public ResponseEntity<Void> deleteNews(@PathVariable UUID id) {
        Optional<News> newsOptional = newsService.getNewsById(id);
        if (newsOptional.isPresent()) {
            News news = newsOptional.get();
            newsService.deleteNews(id);
            try {
                newsService.deleteImage(news.getThumbnail());
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

        Resource resource = new ClassPathResource("static/dataImage/news/" + thumbnail);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate image media type

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
