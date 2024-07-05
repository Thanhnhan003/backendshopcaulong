package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.Banners;
import com.nguyenthanhnhan.backendshopcaulong.service.banner.BannersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/banners")
public class BannersController {

    @Autowired
    private BannersService bannersService;

    @PostMapping("/add")
    public Banners addBanner(
            @RequestParam String bannerName,
            @RequestParam String bannerDescription,
            @RequestParam MultipartFile file,
            @RequestParam boolean enabled,
            @RequestParam String urlLink) throws IOException {
        return bannersService.addBanner(bannerName, bannerDescription, file, enabled, urlLink);
    }

    @PutMapping("/update/{id}")
    public Banners updateBanner(
            @PathVariable UUID id,
            @RequestParam String bannerName,
            @RequestParam String bannerDescription,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam boolean enabled,
            @RequestParam String urlLink) throws IOException {
        return bannersService.updateBanner(id, bannerName, bannerDescription, file, enabled, urlLink);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBanner(@PathVariable UUID id) {
        bannersService.deleteBanner(id);
    }

    @GetMapping("/all")
    public List<Banners> getAllBanners() {
        return bannersService.getAllBanners();
    }

    @GetMapping("/enabled")
    public List<Banners> getEnabledBanners() {
        return bannersService.getEnabledBanners();
    }

    @PutMapping("/update/enabled/{id}")
    public Banners updateBannerEnabledStatus(
            @PathVariable UUID id,
            @RequestParam boolean enabled) {
        return bannersService.updateBannerEnabledStatus(id, enabled);
    }
}
