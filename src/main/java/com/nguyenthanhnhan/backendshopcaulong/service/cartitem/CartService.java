package com.nguyenthanhnhan.backendshopcaulong.service.cartitem;

import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.CartItemRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.ProductRepository;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private JwtService jwtService;

    public List<CartItem> getCartItems(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        return cartItemRepository.findByUser(user);
    }

    public CartItem addProductToCart(String token, UUID productId, int quantity) {
        String email = jwtService.extractUsername(token);
        Optional<Users> optionalUser = userInfoRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User không tìm thấy");
        }

        Users user = optionalUser.get();
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }

        Product product = optionalProduct.get();

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndProduct(user, product);
        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            int newQuantity = existingCartItem.getQuantity() + quantity;
            if (newQuantity > product.getQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds available stock");
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setTotalPrice(String.valueOf(Integer.parseInt(product.getProductPrice()) * newQuantity));
            return cartItemRepository.save(existingCartItem);
        } else {
            if (quantity > product.getQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds available stock");
            }
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getProductPrice());
            cartItem.setTotalPrice(String.valueOf(Integer.parseInt(product.getProductPrice()) * quantity));
            return cartItemRepository.save(cartItem);
        }
    }

    // Fetch cart items by user ID
    public List<CartItem> getCartItemsByUserId(UUID userId) {
        Users user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));
        return cartItemRepository.findByUser(user);
    }

    // Clear cart by user ID
    public void clearCartByUserId(UUID userId) {
        Users user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));
        cartItemRepository.deleteByUser(user);
    }
}
