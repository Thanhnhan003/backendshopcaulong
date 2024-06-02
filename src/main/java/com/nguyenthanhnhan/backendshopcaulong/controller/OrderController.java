// OrderController.java
package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.*;
import com.nguyenthanhnhan.backendshopcaulong.service.cartitem.CartService;
import com.nguyenthanhnhan.backendshopcaulong.service.order.OrderService;
import com.nguyenthanhnhan.backendshopcaulong.service.product.ProductService;
import com.nguyenthanhnhan.backendshopcaulong.service.vnpay.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @GetMapping("/payment")
    public ResponseEntity<Map<String, String>> createPayment(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        List<CartItem> cartItems = cartService.getCartItems(token);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng của bạn trống");
        }

        long totalAmount = cartItems.stream()
                .mapToLong(item -> Integer.parseInt(item.getPrice()) * item.getQuantity())
                .sum();

        UUID userId = cartItems.get(0).getUser().getUserId();
        String orderInfo = "Thanh toán đơn hàng";
        String paymentUrl = vnPayService.createPaymentUrl(request, totalAmount, "", orderInfo, userId);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xử lý yêu cầu từ frontend React
    @GetMapping("/return")
    public ResponseEntity<String> handlePaymentReturn(@RequestParam Map<String, String> params) {
        String userIdParam = params.get("userId");
        UUID userId = UUID.fromString(userIdParam);
        Users user = orderService.findUserById(userId);
        String txnRef = params.get("vnp_TxnRef");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");

        if (orderService.isTransactionProcessed(txnRef)) {
            return ResponseEntity.ok("Cảm ơn bạn đã mua hàng!");
        }

        if ("00".equals(vnp_ResponseCode)) {
            List<CartItem> cartItems = orderService.getCartItemsByUser(user);
            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setTxnRef(txnRef);
            orderService.saveOrder(newOrder);

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(newOrder);
                detail.setProduct(cartItem.getProduct());
                detail.setQuantity(cartItem.getQuantity());
                detail.setPrice(cartItem.getPrice());
                orderDetails.add(detail);

                // // Cập nhật số lượng sản phẩm
                // productService.updateProductQuantity(cartItem.getProduct().getProductId(), cartItem.getQuantity());

            }
            
            orderService.saveOrderDetails(orderDetails);
            orderService.clearCartByUserId(user.getUserId());

            return ResponseEntity.ok("Thanh toán thành công. Mã giao dịch: " + vnp_TransactionNo);
        } else {
            return ResponseEntity.ok("Thanh toán không thành công!");
        }
    }
}
