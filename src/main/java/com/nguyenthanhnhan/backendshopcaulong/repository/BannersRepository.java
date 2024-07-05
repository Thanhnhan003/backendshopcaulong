package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Banners;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BannersRepository extends JpaRepository<Banners, UUID> {
    List<Banners> findByEnabled(boolean enabled);
}
