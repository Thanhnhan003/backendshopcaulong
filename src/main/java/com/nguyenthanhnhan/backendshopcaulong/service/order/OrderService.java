// OrderService.java
package com.nguyenthanhnhan.backendshopcaulong.service.order;

import com.nguyenthanhnhan.backendshopcaulong.entity.Order;
import com.nguyenthanhnhan.backendshopcaulong.entity.OrderDetail;
import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.CartItemRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.OrderDetailRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.OrderRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import com.nguyenthanhnhan.backendshopcaulong.service.cartitem.CartService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // OrderService.java
    public Users findUserById(UUID userId) {
        return userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    public Users findUserByToken(String token) {
        String email = jwtService.extractUsername(token);
        return userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    public List<OrderDetail> saveOrderDetails(List<OrderDetail> orderDetails) {
        return orderDetailRepository.saveAll(orderDetails);
    }

    public List<CartItem> getCartItemsByUser(Users user) {
        return cartService.getCartItemsByUserId(user.getUserId());
    }

    @Transactional
    public void clearCartByUserId(UUID userId) {
        Users user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));
        cartItemRepository.deleteByUser(user);
    }

    public boolean isTransactionProcessed(String txnRef) {
        return orderRepository.existsByTxnRef(txnRef);

    }

    ///của order COD 

    public Order findOrderByTxnRef(String txnRef) {
        return orderRepository.findByTxnRef(txnRef);
    }

    public List<OrderDetail> findOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }



}
