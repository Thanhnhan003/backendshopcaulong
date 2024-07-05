package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "News"
            ));
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading image");
        }
    }

    @GetMapping("/images")
    public ResponseEntity<?> getImages() {
        try {
            ApiResponse apiResponse = cloudinary.search()
                    .expression("folder:News/*")
                    .execute();
            List<Map> resources = (List<Map>) apiResponse.get("resources");
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving images");
        }
    }
}
