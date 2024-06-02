package com.nguyenthanhnhan.backendshopcaulong.service.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.repository.ProductRepository;
import com.github.slugify.Slugify;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private Slugify slugify = new Slugify();

    public Product createProduct(Product product) {
        if (productRepository.existsByProductName(product.getProductName())) {
            throw new IllegalArgumentException("Product name already exists.");
        }
        product.setSlug(generateUniqueSlug(product.getProductName()));
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!existingProduct.getProductName().equals(product.getProductName()) &&
                productRepository.existsByProductName(product.getProductName())) {
            throw new IllegalArgumentException("Product name already exists.");
        }
        product.setSlug(generateUniqueSlug(product.getProductName()));
        return productRepository.save(product);
    }

    private String generateUniqueSlug(String name) {
        String slug = slugify.slugify(name);
        int count = 0;
        while (productRepository.existsBySlug(slug)) {
            slug = slugify.slugify(name) + "-" + (++count);
        }
        return slug;
    }

    public Product getProductById(UUID productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);
    }

    public Product getProductBySlug(String slug) {
        Optional<Product> product = productRepository.findBySlug(slug);
        return product.orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop8ByOrderByCreatedAtDesc();
    }

    // Cập nhật số lượng sản phẩm sau khi bán
    @Transactional
    public void updateProductQuantity(UUID productId, int quantitySold) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tìm thấy"));

        if (product.getQuantity() < quantitySold) {
            throw new IllegalArgumentException("Không đủ hàng tồn kho");
        }

        product.setQuantity(product.getQuantity() - quantitySold);
        productRepository.save(product);
    }
}