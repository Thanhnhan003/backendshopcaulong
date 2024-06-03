package com.nguyenthanhnhan.backendshopcaulong.service.Address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.repository.DeliveryAddressUserRepository;

@Service
public class DeliveryAddressUserService {
    @Autowired
    private DeliveryAddressUserRepository deliveryAddressUserRepository;

    public List<DeliveryAddressUser> getAllAddress() {
        return deliveryAddressUserRepository.findAll();
    }
}
