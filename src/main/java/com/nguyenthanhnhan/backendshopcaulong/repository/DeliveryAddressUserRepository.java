package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryAddressUserRepository extends JpaRepository<DeliveryAddressUser, UUID> {

    List<DeliveryAddressUser> findByUsers(Users user);
}
