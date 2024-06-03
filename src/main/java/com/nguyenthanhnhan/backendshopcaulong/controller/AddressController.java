package com.nguyenthanhnhan.backendshopcaulong.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.service.Address.DeliveryAddressUserService;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private DeliveryAddressUserService deliveryAddressUserService;

    @GetMapping()
    public List<DeliveryAddressUser> getAllAddress() {
        return deliveryAddressUserService.getAllAddress();
    }
}
