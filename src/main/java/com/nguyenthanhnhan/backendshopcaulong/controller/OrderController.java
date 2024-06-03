// OrderController.java
package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.*;
import com.nguyenthanhnhan.backendshopcaulong.service.cartitem.CartService;
import com.nguyenthanhnhan.backendshopcaulong.service.email.EmailService;
import com.nguyenthanhnhan.backendshopcaulong.service.order.OrderService;
import com.nguyenthanhnhan.backendshopcaulong.service.product.ProductService;
import com.nguyenthanhnhan.backendshopcaulong.service.vnpay.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.text.NumberFormat;

import jakarta.mail.MessagingException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

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
    @Autowired
    private EmailService emailService;

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

            }
            orderService.saveOrderDetails(orderDetails);
            orderService.clearCartByUserId(user.getUserId());
            // Generate email content
            String emailBody = generateOrderConfirmationEmailBody(user, newOrder, orderDetails);

            // Send the email
            emailService.sendOrderConfirmationEmail(user.getEmail(), "Xác nhận đơn hàng của bạn", emailBody);

            return ResponseEntity.ok("Thanh toán thành công. Mã giao dịch: " + vnp_TransactionNo);
        } else {
            return ResponseEntity.ok("Thanh toán không thành công!");
        }
    }

    // OrderController.java
    // OrderController.java

    @PostMapping("/cod")
    public ResponseEntity<String> processCodPayment(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            List<CartItem> cartItems = cartService.getCartItems(token);

            if (cartItems.isEmpty()) {
                throw new IllegalArgumentException("Giỏ hàng của bạn trống");
            }

            UUID userId = cartItems.get(0).getUser().getUserId();
            Users user = orderService.findUserById(userId);

            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setTxnRef(UUID.randomUUID().toString()); // Generate a random transaction reference
            orderService.saveOrder(newOrder);

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(newOrder);
                detail.setProduct(cartItem.getProduct());
                detail.setQuantity(cartItem.getQuantity());
                detail.setPrice(cartItem.getPrice());
                orderDetails.add(detail);
            }
            orderService.saveOrderDetails(orderDetails);
            orderService.clearCartByUserId(user.getUserId());

            // Generate email content
            String emailBody = generateOrderConfirmationEmailBody(user, newOrder, orderDetails);

            // Send the email
            emailService.sendOrderConfirmationEmail(user.getEmail(), "Xác nhận đơn hàng của bạn", emailBody);

            return ResponseEntity.ok("Đơn hàng của bạn đã được xác nhận và sẽ được thanh toán khi nhận hàng.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log chi tiết lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi nội bộ. Vui lòng thử lại sau.");
        }
    }

    @Transactional
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam("txnRef") String txnRef,
            @RequestParam("userId") UUID userId) {
        Order order = orderService.findOrderByTxnRef(txnRef);

        if (order == null) {
            Users user = orderService.findUserById(userId);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            // Create a new order
            order = new Order();
            order.setUser(user);
            order.setTxnRef(txnRef);
            orderService.saveOrder(order);

            List<CartItem> cartItems = orderService.getCartItemsByUser(user);
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("No items in cart for user.");
            }

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(cartItem.getProduct());
                detail.setQuantity(cartItem.getQuantity());
                detail.setPrice(cartItem.getPrice());
                orderDetails.add(detail);
            }

            orderService.saveOrderDetails(orderDetails);
            orderService.clearCartByUserId(user.getUserId());

            return ResponseEntity.ok("Đơn hàng đã được xác nhận. Cảm ơn bạn đã mua hàng!");
        } else {
            return ResponseEntity.badRequest().body("Order has already been confirmed.");
        }
    }

    private String generateOrderConfirmationEmailBody(Users user, Order order, List<OrderDetail> orderDetails) {
        StringBuilder body = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        body.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { background-color: #eaeaea; font-family: Arial, sans-serif; }")
                .append(".container { background-color: white; padding: 20px; margin: 20px auto; width: 80%; border: 1px solid #ddd; }")
                .append("h1 { text-align: center; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("table, th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #f2f2f2; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h1>Thông tin đơn hàng của bạn!</h1>")
                .append("<p><strong>Họ tên:</strong> ").append(user.getFullname()).append("</p>")
                .append("<p><strong>Thời gian đặt:</strong> ").append(order.getOrderTime()).append("</p>")
                .append("<table>")
                .append("<tr><th>Tên SP</th><th>Số lượng</th><th>Giá</th></tr>");

        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderDetail detail : orderDetails) {
            BigDecimal price = new BigDecimal(detail.getPrice());
            body.append("<tr>")
                    .append("<td>").append(detail.getProduct().getProductName()).append("</td>")
                    .append("<td>").append(detail.getQuantity()).append("</td>")
                    .append("<td>").append(currencyFormat.format(price)).append("</td>")
                    .append("</tr>");
            totalQuantity += detail.getQuantity();
            totalPrice = totalPrice.add(price);
        }

        body.append("<tr><th>Tổng cộng:</th><th>").append(totalQuantity).append("</th><th>")
                .append(currencyFormat.format(totalPrice))
                .append("</th></tr>")
                .append("</table>")
                .append("<p>Xin cảm ơn!</p>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return body.toString();
    }
}
