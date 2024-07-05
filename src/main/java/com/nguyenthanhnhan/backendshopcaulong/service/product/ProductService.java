package com.nguyenthanhnhan.backendshopcaulong.service.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.entity.Categories;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.repository.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.github.slugify.Slugify;

import java.math.BigDecimal;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

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
        return productRepository.findTop10ByOrderByCreatedAtDesc();
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

    /// =================================
    public static Specification<Product> hasCategorySlugs(List<String> slugs) {
        return (root, query, criteriaBuilder) -> root.get("category").get("slug").in(slugs);
    }

    public static Specification<Product> hasBrandSlugs(List<String> slugs) {
        return (root, query, criteriaBuilder) -> root.get("brand").get("slug").in(slugs);
    }

    public List<Product> searchProductsByName(String productName) {
        return productRepository.findByProductNameContainingIgnoreCase(productName);
    }

    /// =================================
    public List<CategoryWithBrands> getCategoriesWithBrandsDetails() {
        List<Product> products = productRepository.findAll();

        Map<Categories, Set<Brand>> categoryBrandMap = new LinkedHashMap<>();

        for (Product product : products) {
            Categories category = product.getCategory();
            Brand brand = product.getBrand();

            categoryBrandMap.computeIfAbsent(category, k -> new LinkedHashSet<>()).add(brand);
        }

        List<CategoryWithBrands> categoryWithBrandsList = new ArrayList<>();

        for (Map.Entry<Categories, Set<Brand>> entry : categoryBrandMap.entrySet()) {
            categoryWithBrandsList.add(new CategoryWithBrands(entry.getKey(), new ArrayList<>(entry.getValue())));
        }

        return categoryWithBrandsList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryWithBrands {
        private Categories category;
        private List<Brand> brands;
    }

    /// ======================================================================
    /// Sản phẩm tương tự dựa vào chi tiết sản phẩm
    public List<Product> getProductsByCategoryAndBrand(UUID categoryId, UUID brandId) {
        return productRepository.findByCategoryCategoryIdAndBrandBrandId(categoryId, brandId);
    }

    // Bỏ hiển thị sản phẩm có slug lấy kết quả sản phẩm tương tự
    public List<Product> getSimilarProductsExcludingCurrent(String slug) {
        Product product = getProductBySlug(slug);
        if (product != null) {
            List<Product> similarProducts = getProductsByCategoryAndBrand(product.getCategory().getCategoryId(),
                    product.getBrand().getBrandId());
            return similarProducts.stream()
                    .filter(p -> !p.getProductId().equals(product.getProductId()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    // Hiển thị các sản phẩm sắp hết số lượng cần nhập hàng
    public List<Product> getProductsWithLowQuantity() {
        return productRepository.findProductsWithLowQuantity();
    }

    // lọc theo giá
    public static Specification<Product> hasPriceBetween(String minPrice, String maxPrice) {
        return (root, query, criteriaBuilder) -> {
            BigDecimal min = new BigDecimal(minPrice);
            BigDecimal max = new BigDecimal(maxPrice);
            return criteriaBuilder.between(root.get("productPrice").as(BigDecimal.class), min, max);
        };
    }

    // Danh sách sản phẩm dựa vào slug category và lọc theo giá
    public List<Product> getProductsByCategoryAndBrandSlug(String categorySlug, String brandSlug, String minPrice,
            String maxPrice) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategorySlug(categorySlug))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

        if (brandSlug != null && !brandSlug.isEmpty()) {
            spec = spec.and(ProductSpecification.hasBrandSlug(brandSlug));
        }

        return productRepository.findAll(spec);
    }
}