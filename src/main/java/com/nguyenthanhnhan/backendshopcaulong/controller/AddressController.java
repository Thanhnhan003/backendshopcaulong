package com.nguyenthanhnhan.backendshopcaulong.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.service.address.DeliveryAddressUserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private DeliveryAddressUserService deliveryAddressUserService;

    @GetMapping()
    public List<DeliveryAddressUser> getAllAddress() {
        return deliveryAddressUserService.getAllAddress();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAddressByUser(HttpServletRequest request) {
        try {
            // Trích xuất mã thông báo từ tiêu đề yêu cầu
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization header không tồn tại hoặc không bắt đầu bằng Bearer");
            }
            String token = authHeader.substring(7);
            // Lấy địa chỉ
            List<DeliveryAddressUser> addresses = deliveryAddressUserService.getAddressByUser(token);
            return ResponseEntity.ok(addresses);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi lấy địa chỉ giao hàng");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAddress(HttpServletRequest request, @RequestBody DeliveryAddressUser address) {
        try {
            // Extract the token from the request header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization header không tồn tại hoặc không bắt đầu bằng Bearer");
            }
            String token = authHeader.substring(7);

            // Add the address
            deliveryAddressUserService.addAddressbyUser(token, address);
            return ResponseEntity.status(HttpStatus.CREATED).body("Địa chỉ giao hàng đã được thêm thành công");

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi thêm địa chỉ giao hàng");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAddress(HttpServletRequest request, @RequestParam UUID addressId) {
        try {
            // Extract the token from the request header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization header không tồn tại hoặc không bắt đầu bằng Bearer");
            }
            String token = authHeader.substring(7);

            // Delete the address
            deliveryAddressUserService.deleteAddressByUser(token, addressId);
            return ResponseEntity.ok("Địa chỉ giao hàng đã được xóa thành công");

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi xóa địa chỉ giao hàng");
        }
    }
}
