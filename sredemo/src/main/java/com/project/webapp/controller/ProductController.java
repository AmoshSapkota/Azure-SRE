package com.project.webapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;

@RestController
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductService service;

    @RequestMapping("/products")
    public ResponseEntity<List<Product>> getProduct() {
        try {
            logger.info("GET /products - Retrieving all products");
            List<Product> products = service.getProduct();
            logger.info("GET /products - Successfully returned {} products", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("GET /products - Error retrieving products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping("/products/{prodId}")
    public ResponseEntity<Product> getProductById(@PathVariable int prodId) {
        try {
            logger.info("GET /products/{} - Retrieving product by ID", prodId);
            Product product = service.getProductById(prodId);
            
            if (product != null && product.getProdId() != 0) {
                logger.info("GET /products/{} - Product found: {}", prodId, product.getProdName());
                return ResponseEntity.ok(product);
            } else {
                logger.warn("GET /products/{} - Product not found", prodId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("GET /products/{} - Error retrieving product", prodId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product prod) {
        try {
            // Basic validation
            if (prod.getProdName() == null || prod.getProdName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (prod.getPrice() < 0) {
                return ResponseEntity.badRequest().build();
            }
            
            Product savedProduct = service.addProduct(prod);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/products/{prodId}")
    public ResponseEntity<Product> updateProduct(@PathVariable int prodId, @RequestBody Product prod){
        Product updatedProduct = service.updateProduct(prodId, prod);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/products/{prodId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int prodId){
        service.deleteProduct(prodId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/products/{prodId}")
    public ResponseEntity<Product> updateProductPartially(@PathVariable int prodId, @RequestBody Product prod) {
        Product updatedProduct = service.updateProductPartially(prodId, prod);
        return ResponseEntity.ok(updatedProduct);
    }
}