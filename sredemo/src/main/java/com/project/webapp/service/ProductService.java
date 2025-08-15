package com.project.webapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    ProductRepo repo;
    
    @Autowired
    TelemetryService telemetryService;
    
    public List<Product> getProduct() {
        return telemetryService.executeWithSpan("product.getAll", span -> {
            logger.info("Retrieving all products");
            telemetryService.addSpanAttributes("operation", "get_all_products");
            
            // Record database operation
            telemetryService.recordDatabaseOperation("findAll", "started");
            
            List<Product> products = repo.findAll();
            
            // Record successful operation
            telemetryService.recordDatabaseOperation("findAll", "success");
            telemetryService.recordProductOperation("get_all", "all_categories", "success");
            
            // Add span attributes
            telemetryService.addSpanAttributes("product.count", String.valueOf(products.size()));
            long categoryCount = products.stream().map(Product::getCategory).distinct().count();
            telemetryService.addSpanAttributes("category.count", String.valueOf(categoryCount));
            
            logger.info("Successfully retrieved {} products. Categories: {}", 
                products.size(), categoryCount);
            
            return products;
        });
    }

    public Product getProductById(int prodId) {
        return telemetryService.executeWithSpan("product.getById", span -> {
            logger.info("Retrieving product with ID: {}", prodId);
            telemetryService.addSpanAttributes("operation", "get_by_id");
            telemetryService.addSpanAttributes("product.id", String.valueOf(prodId));
            
            // Record database operation
            telemetryService.recordDatabaseOperation("findById", "started");
            
            Product product = repo.findById(prodId).orElse(null);
            
            if (product != null) {
                telemetryService.recordDatabaseOperation("findById", "success");
                telemetryService.recordProductOperation("get_by_id", product.getCategory(), "success");
                telemetryService.addProductAttributes((long) product.getProdId(), 
                    product.getProdName(), (double) product.getPrice(), product.getCategory());
                
                logger.info("Successfully retrieved product: {} (Category: {}, Price: {})", 
                    product.getProdName(), product.getCategory(), product.getPrice());
            } else {
                telemetryService.recordDatabaseOperation("findById", "not_found");
                telemetryService.recordProductOperation("get_by_id", "unknown", "not_found");
                logger.warn("Product not found with ID: {}", prodId);
            }
            
            return product;
        });
    }

    public Product addProduct(Product prod) {
        if (prod == null) {
            telemetryService.recordProductOperation("add", "unknown", "error");
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        return telemetryService.executeWithSpan("product.add", span -> {
            logger.info("Adding new product: {} (Category: {}, Price: {})", 
                prod.getProdName(), prod.getCategory(), prod.getPrice());
            
            // Add product attributes to span
            telemetryService.addProductAttributes(null, prod.getProdName(), 
                (double) prod.getPrice(), prod.getCategory());
            telemetryService.addSpanAttributes("operation", "add_product");
            
            // Record database operation
            telemetryService.recordDatabaseOperation("save", "started");
            
            Product savedProduct = repo.save(prod);
            
            // Record successful operations and metrics
            telemetryService.recordDatabaseOperation("save", "success");
            telemetryService.recordProductOperation("add", prod.getCategory(), "success");
            telemetryService.recordProductPrice(prod.getPrice(), prod.getCategory());
            
            // Update span with final product ID
            telemetryService.addProductAttributes((long) savedProduct.getProdId(), null, null, null);
            
            logger.info("Successfully added product: {} with ID: {}", 
                savedProduct.getProdName(), savedProduct.getProdId());
                
            return savedProduct;
        });
    }
    
    public Product updateProduct(int prodId, Product prod) {
        logger.info("Updating product with ID: {} to name: {}", prodId, prod.getProdName());
        
        prod.setProdId(prodId);
        Product updatedProduct = repo.save(prod);
        
        logger.info("Successfully updated product with ID: {}", prodId);
        return updatedProduct;
    }
    
    public void deleteProduct(int prodId) {
        logger.info("Deleting product with ID: {}", prodId);
        repo.deleteById(prodId);
        logger.info("Successfully deleted product with ID: {}", prodId);
    }
    
    public Product updateProductPartially(int prodId, Product updates) {
        logger.info("Partially updating product with ID: {}", prodId);
        
        Product existing = getProductById(prodId);
        if (existing != null && existing.getProdId() != 0) {
            boolean hasUpdates = false;
            
            if (updates.getProdName() != null && !updates.getProdName().isEmpty()) {
                existing.setProdName(updates.getProdName());
                hasUpdates = true;
            }
            if (updates.getPrice() > 0) {
                existing.setPrice(updates.getPrice());
                hasUpdates = true;
            }
            if (updates.getCategory() != null && !updates.getCategory().isEmpty()) {
                existing.setCategory(updates.getCategory());
                hasUpdates = true;
            }
            
            if (hasUpdates) {
                Product savedProduct = repo.save(existing);
                logger.info("Successfully partially updated product with ID: {}", prodId);
                return savedProduct;
            } else {
                logger.info("No updates applied to product with ID: {}", prodId);
                return existing;
            }
        } else {
            logger.warn("Product not found for partial update with ID: {}", prodId);
            return null;
        }
    }
}