package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserInfoRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
    List<Users> findByRoles(String roles);
    Optional<Users> findByActivationCode(String activationCode);
}
