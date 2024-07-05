package com.nguyenthanhnhan.backendshopcaulong.service.banner;

import com.nguyenthanhnhan.backendshopcaulong.entity.Banners;
import com.nguyenthanhnhan.backendshopcaulong.repository.BannersRepository;
import com.nguyenthanhnhan.backendshopcaulong.service.cloudinary.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BannersService {

    @Autowired
    private BannersRepository bannersRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Banners addBanner(String bannerName, String bannerDescription, MultipartFile file, boolean enabled, String urlLink) throws IOException {
        String urlImg = (String) cloudinaryService.uploadImage(file).get("url");
        Banners banner = new Banners(UUID.randomUUID(), bannerName, bannerDescription, urlImg, urlLink, enabled);
        return bannersRepository.save(banner);
    }

    public Banners updateBanner(UUID id, String bannerName, String bannerDescription, MultipartFile file, boolean enabled, String urlLink) throws IOException {
        Optional<Banners> optionalBanner = bannersRepository.findById(id);
        if (optionalBanner.isPresent()) {
            Banners banner = optionalBanner.get();
            if (file != null && !file.isEmpty()) {
                String urlImg = (String) cloudinaryService.uploadImage(file).get("url");
                banner.setUrlImg(urlImg);
            }
            banner.setBannerName(bannerName);
            banner.setBannerDescription(bannerDescription);
            banner.setEnabled(enabled);
            banner.setUrlLink(urlLink);
            return bannersRepository.save(banner);
        }
        return null;
    }

    public void deleteBanner(UUID id) {
        bannersRepository.deleteById(id);
    }

    public List<Banners> getAllBanners() {
        return bannersRepository.findAll();
    }

    public List<Banners> getEnabledBanners() {
        return bannersRepository.findByEnabled(true);
    }
    public Banners updateBannerEnabledStatus(UUID id, boolean enabled) {
        Optional<Banners> optionalBanner = bannersRepository.findById(id);
        if (optionalBanner.isPresent()) {
            Banners banner = optionalBanner.get();
            banner.setEnabled(enabled);
            return bannersRepository.save(banner);
        }
        return null;
    }
}
