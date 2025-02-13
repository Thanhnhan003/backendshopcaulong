// OrderService.java
package com.nguyenthanhnhan.backendshopcaulong.service.order;

import com.nguyenthanhnhan.backendshopcaulong.entity.Order;
import com.nguyenthanhnhan.backendshopcaulong.entity.OrderDetail;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.DeliveryAddressUser;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.CartItemRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.DeliveryAddressUserRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.OrderDetailRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.OrderRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.ProductRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import com.nguyenthanhnhan.backendshopcaulong.service.cartitem.CartService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    @Autowired
    private ProductRepository productRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    // get đơn hàng dựa vào token người dùng
    public List<Order> getOrderByToken(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        return orderRepository.findByUser(user);
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

    /// của order COD

    public Order findOrderByTxnRef(String txnRef) {
        return orderRepository.findByTxnRef(txnRef);
    }

    public List<OrderDetail> findOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }
    // Phương pháp cập nhật số lượng sản phẩm dựa trên chi tiết đơn hàng
    public void updateProductQuantities(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            product.setQuantity(product.getQuantity() - detail.getQuantity());
            productRepository.save(product);
        }
    }

    @Autowired
    private DeliveryAddressUserRepository deliveryAddressUserRepository;

    // địa chỉ nhận hàng của người dùng
    public DeliveryAddressUser findDeliveryAddressById(UUID addressUserId) {
        return deliveryAddressUserRepository.findById(addressUserId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ người nhận"));
    }

    // xử lý đơn hàng
    public Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    // tính tổng danh thu
    public double calculateTotalRevenue(String status) {
        List<Order> orders = orderRepository.findByStatus(status);

        double totalRevenue = 0.0;
        for (Order order : orders) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            totalRevenue += orderDetails.stream()
                    .mapToDouble(detail -> Double.parseDouble(detail.getPrice()))
                    .sum();
        }
        return totalRevenue;
    }

    // tổng số lượng đơn hàng chưa xử lý
    public long countOrdersWithStatusEqualTo(String status) {
        return orderRepository.findByStatus(status).size();
    }
}
