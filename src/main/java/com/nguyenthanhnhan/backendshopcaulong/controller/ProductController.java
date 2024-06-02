package com.nguyenthanhnhan.backendshopcaulong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyenthanhnhan.backendshopcaulong.entity.Brand;
import com.nguyenthanhnhan.backendshopcaulong.entity.Categories;
import com.nguyenthanhnhan.backendshopcaulong.entity.Gallery;
import com.nguyenthanhnhan.backendshopcaulong.entity.Product;
import com.nguyenthanhnhan.backendshopcaulong.service.brand.BrandService;
import com.nguyenthanhnhan.backendshopcaulong.service.category.CategoriesService;
import com.nguyenthanhnhan.backendshopcaulong.service.gallery.GalleryService;
import com.nguyenthanhnhan.backendshopcaulong.service.product.ProductService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private GalleryService galleryService;
    @Autowired
    private CategoriesService categoriesService; // Assuming this service exists
    @Autowired
    private BrandService brandService; // Assuming this service exists

    @PostMapping("/create")
    public ResponseEntity<String> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("productPrice") BigDecimal productPrice,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("brandId") UUID brandId,
            @RequestParam("images") List<MultipartFile> images) {

        try {
            // Retrieve Category and Brand
            Categories category = categoriesService.findById(categoryId);
            Brand brand = brandService.findById(brandId);

            if (category == null || brand == null) {
                return new ResponseEntity<>("Category or Brand not found.", HttpStatus.BAD_REQUEST);
            }

            // Create a new product and set its properties
            Product product = new Product();
            product.setProductName(name);
            product.setProductPrice(productPrice.toString());
            product.setQuantity(quantity);
            product.setProductDescription(productDescription);
            product.setCategory(category);
            product.setBrand(brand);

            // Save the product to generate its ID
            product = productService.createProduct(product);

            // Upload images and create gallery entries
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                    String uploadDir = "src/main/resources/static/dataImage/products/";
                    File uploadPath = new File(uploadDir);
                    if (!uploadPath.exists()) {
                        uploadPath.mkdirs();
                    }
                    File dest = new File(uploadDir + fileName);
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(image.getBytes());
                    }

                    // Create Gallery object and link it to the product
                    Gallery gallery = new Gallery();
                    gallery.setThumbnail(fileName);
                    gallery.setProduct(product); // Link the gallery to the product
                    galleryService.createGallery(gallery);
                }
            }

            return new ResponseEntity<>("Thêm sản phẩm thành công!", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Lỗi thêm sản phẩm.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable UUID productId,
            @RequestParam("name") String name,
            @RequestParam("productPrice") BigDecimal productPrice,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("brandId") UUID brandId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        try {
            // Retrieve existing product
            Product existingProduct = productService.getProductById(productId);
            if (existingProduct == null) {
                return new ResponseEntity<>("Product not found.", HttpStatus.NOT_FOUND);
            }

            // Retrieve Category and Brand
            Categories category = categoriesService.findById(categoryId);
            Brand brand = brandService.findById(brandId);

            if (category == null || brand == null) {
                return new ResponseEntity<>("Category or Brand not found.", HttpStatus.BAD_REQUEST);
            }

            // Update product properties
            existingProduct.setProductName(name);
            existingProduct.setProductPrice(productPrice.toString());
            existingProduct.setQuantity(quantity);
            existingProduct.setProductDescription(productDescription);
            existingProduct.setCategory(category);
            existingProduct.setBrand(brand);

            // Handle image updates if any
            if (images != null && !images.isEmpty()) {
                // Clear existing galleries
                existingProduct.getGalleries().clear();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        String uploadDir = "src/main/resources/static/dataImage/products/";
                        File uploadPath = new File(uploadDir);
                        if (!uploadPath.exists()) {
                            uploadPath.mkdirs();
                        }
                        File dest = new File(uploadDir + fileName);
                        try (FileOutputStream fos = new FileOutputStream(dest)) {
                            fos.write(image.getBytes());
                        }

                        // Create Gallery object and link it to the product
                        Gallery gallery = new Gallery();
                        gallery.setThumbnail(fileName);
                        gallery.setProduct(existingProduct); // Link the gallery to the product
                        galleryService.createGallery(gallery);
                    }
                }
            }

            // Save updated product
            productService.updateProduct(existingProduct);

            return new ResponseEntity<>("Product updated successfully.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update product.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/show/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/images/{thumbnail}")
    public ResponseEntity<byte[]> getImage(@PathVariable String thumbnail) throws IOException {
        // You should sanitize and validate the thumbnail parameter to prevent directory
        // traversal attacks.
        // In this example, it is assumed that thumbnail is just the name of the image
        // file.

        Resource resource = new ClassPathResource("static/dataImage/products/" + thumbnail);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate image media type

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/show")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null) {
                // Retrieve and delete associated gallery images
                List<Gallery> galleries = product.getGalleries();
                String uploadDir = "src/main/resources/static/dataImage/products/";
                for (Gallery gallery : galleries) {
                    File file = new File(uploadDir + gallery.getThumbnail());
                    if (file.exists()) {
                        file.delete();
                    }
                    galleryService.deleteGallery(gallery.getGalleryId());
                }

                // Delete the product
                productService.deleteProduct(productId);
                return new ResponseEntity<>("Product deleted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete product.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Product>> getLatestProducts() {
        List<Product> products = productService.getLatestProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Product> getProductBySlug(@PathVariable String slug) {
        Product product = productService.getProductBySlug(slug);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
