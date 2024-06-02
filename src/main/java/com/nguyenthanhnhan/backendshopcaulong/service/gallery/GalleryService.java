package com.nguyenthanhnhan.backendshopcaulong.service.gallery;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nguyenthanhnhan.backendshopcaulong.entity.Gallery;
import com.nguyenthanhnhan.backendshopcaulong.repository.GalleryRepository;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    public Gallery createGallery(Gallery gallery) {
        return galleryRepository.save(gallery);
    }
        public void deleteGallery(UUID id) {
        galleryRepository.deleteById(id);
    }
}
