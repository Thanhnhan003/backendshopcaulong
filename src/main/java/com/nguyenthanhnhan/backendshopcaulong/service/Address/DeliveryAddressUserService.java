package com.nguyenthanhnhan.backendshopcaulong.service.address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.DeliveryAddressUserRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;

@Service
public class DeliveryAddressUserService {
    @Autowired
    private DeliveryAddressUserRepository deliveryAddressUserRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public List<DeliveryAddressUser> getAllAddress() {
        return deliveryAddressUserRepository.findAll();
    }

    public List<DeliveryAddressUser> getAddressByUser(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        return deliveryAddressUserRepository.findByUsers(user);
    }

    public DeliveryAddressUser addAddressbyUser(String token, DeliveryAddressUser address) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        address.setUsers(user);
        return deliveryAddressUserRepository.save(address);
    }

    public void deleteAddressByUser(String token, UUID addressId) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        Optional<DeliveryAddressUser> optionalAddress = deliveryAddressUserRepository.findById(addressId);

        if (optionalAddress.isEmpty() || !optionalAddress.get().getUsers().equals(user)) {
            throw new IllegalArgumentException("Địa chỉ không tồn tại hoặc không thuộc về người dùng này");
        }

        deliveryAddressUserRepository.deleteById(addressId);
    }
}
