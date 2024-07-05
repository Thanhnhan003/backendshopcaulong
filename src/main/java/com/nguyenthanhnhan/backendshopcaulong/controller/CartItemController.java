package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.service.cartitem.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartItemController {

    @Autowired
    private CartService cartService;

    // @PostMapping("/add")
    // public ResponseEntity<?> addProductToCart(HttpServletRequest request,
    // @RequestParam("productId") UUID productId,
    // @RequestParam("quantity") int quantity) {
    // String authHeader = request.getHeader("Authorization");
    // String token = authHeader != null && authHeader.startsWith("Bearer ") ?
    // authHeader.substring(7) : null;

    // if (token == null) {
    // return ResponseEntity.badRequest().body("Token is missing or invalid");
    // }
    // try {
    // CartItem cartItem = cartService.addProductToCart(token, productId, quantity);
    // return ResponseEntity.ok(cartItem);
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }
    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(HttpServletRequest request,
            @RequestParam("productId") UUID productId,
            @RequestParam("quantity") int quantity) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        if (token == null) {
            return ResponseEntity.badRequest().body("Token is missing or invalid");
        }
        try {
            Optional<Product> optionalProduct = cartService.getProductById(productId);
            if (optionalProduct.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm    x");
            }

            Product product = optionalProduct.get();
            if (quantity > product.getQuantity()) {
                return ResponseEntity.badRequest().body("Số lượng trong kho không đủ");
            }

            CartItem cartItem = cartService.addProductToCart(token, productId, quantity);
            return ResponseEntity.ok(cartItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        if (token == null) {
            return ResponseEntity.badRequest().body("Token is missing or invalid");
        }

        try {
            List<CartItem> cartItems = cartService.getCartItems(token);
            return ResponseEntity.ok(cartItems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeProductFromCart(HttpServletRequest request,
            @RequestParam("productId") UUID productId) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;

        if (token == null) {
            return ResponseEntity.badRequest().body("Token is missing or invalid");
        }

        try {
            cartService.removeProductFromCart(token, productId);
            return ResponseEntity.ok("Product removed from cart successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
