package com.project.webapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
 
@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    ProductRepo repo;
    
    // Manual OpenTelemetry SDK setup (works with agent)
    private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
    private final Tracer tracer = openTelemetry.getTracer("azure-sre-demo", "1.0.0");
    private final Meter meter = openTelemetry.getMeter("azure-sre-demo");
    
    // Custom business metrics
    private final LongCounter productRetrievalCounter = meter
        .counterBuilder("products.retrieved")
        .setDescription("Number of products retrieved")
        .build();
    
    private final LongCounter productCreationCounter = meter
        .counterBuilder("products.created")
        .setDescription("Number of products created")
        .build();
    
    private final LongCounter productQueryCounter = meter
        .counterBuilder("products.queried")
        .setDescription("Number of individual product queries")
        .build();
    
    public List<Product> getProduct() {
        // Custom span with business context
        Span span = tracer.spanBuilder("get-all-products")
            .setAttribute("operation.type", "database.query")
            .setAttribute("service.component", "product-service")
            .startSpan();
        
        try {
            logger.info("Retrieving all products");
            List<Product> products = repo.findAll();
            
            // Custom metrics
            productRetrievalCounter.add(1);
            
            // Add business context to span
            span.setAttribute("products.count", products.size());
            span.setStatus(StatusCode.OK);
            
            // Structured logging with business context
            logger.info("Successfully retrieved {} products. Categories: {}", 
                products.size(), 
                products.stream().map(Product::getCategory).distinct().count());
            
            return products;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error retrieving products", e);
            throw e;
        } finally {
            span.end();
        }
    }

    public Product getProductById(int prodId) {
        // Custom span with product-specific attributes
        Span span = tracer.spanBuilder("get-product-by-id")
            .setAttribute("product.id", prodId)
            .setAttribute("operation.type", "database.find")
            .startSpan();
        
        try {
            logger.info("Retrieving product with ID: {}", prodId);
            Product product = repo.findById(prodId).orElse(new Product());
            
            // Custom metric
            productQueryCounter.add(1);
            
            if (product.getProdId() != 0) {
                span.setAttribute("product.found", true);
                span.setAttribute("product.name", product.getProdName());
                span.setAttribute("product.category", product.getCategory());
                span.setAttribute("product.price", product.getPrice());
                
                logger.info("Successfully retrieved product: {} (Category: {}, Price: {})", 
                    product.getProdName(), product.getCategory(), product.getPrice());
            } else {
                span.setAttribute("product.found", false);
                logger.warn("Product not found with ID: {}", prodId);
            }
            
            span.setStatus(StatusCode.OK);
            return product;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error retrieving product with ID: {}", prodId, e);
            throw e;
        } finally {
            span.end();
        }
    }

    public Product addProduct(Product prod) {
        // Custom span with product creation context
        Span span = tracer.spanBuilder("create-product")
            .setAttribute("product.name", prod.getProdName())
            .setAttribute("product.category", prod.getCategory())
            .setAttribute("product.price", prod.getPrice())
            .setAttribute("operation.type", "database.insert")
            .startSpan();
        
        try {
            logger.info("Adding new product: {} (Category: {}, Price: {})", 
                prod.getProdName(), prod.getCategory(), prod.getPrice());
            
            Product savedProduct = repo.save(prod);
            
            // Custom business metrics
            productCreationCounter.add(1);
            
            // Add success attributes to span
            span.setAttribute("product.id", savedProduct.getProdId());
            span.setAttribute("operation.success", true);
            span.setStatus(StatusCode.OK);
            
            // Structured logging with business metrics
            logger.info("Successfully added product: {} with ID: {}", 
                savedProduct.getProdName(), savedProduct.getProdId());
                
            return savedProduct;
                
        } catch (Exception e) {
            span.setAttribute("operation.success", false);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error adding product: {}", prod.getProdName(), e);
            throw e;
        } finally {
            span.end();
        }
    }
    
    public Product updateProduct(int prodId, Product prod) {
        // Custom span for update operations
        Span span = tracer.spanBuilder("update-product")
            .setAttribute("product.id", prodId)
            .setAttribute("product.name", prod.getProdName())
            .setAttribute("operation.type", "database.update")
            .startSpan();
        
        try {
            logger.info("Updating product with ID: {} to name: {}", prodId, prod.getProdName());
            
            prod.setProdId(prodId);
            Product updatedProduct = repo.save(prod);
            
            span.setAttribute("operation.success", true);
            span.setStatus(StatusCode.OK);
            
            logger.info("Successfully updated product with ID: {}", prodId);
            return updatedProduct;
        } catch (Exception e) {
            span.setAttribute("operation.success", false);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error updating product with ID: {}", prodId, e);
            throw e;
        } finally {
            span.end();
        }
    }
    
    public void deleteProduct(int prodId) {
        // Custom span for delete operations
        Span span = tracer.spanBuilder("delete-product")
            .setAttribute("product.id", prodId)
            .setAttribute("operation.type", "database.delete")
            .startSpan();
        
        try {
            logger.info("Deleting product with ID: {}", prodId);
            
            repo.deleteById(prodId);
            
            span.setAttribute("operation.success", true);
            span.setStatus(StatusCode.OK);
            
            logger.info("Successfully deleted product with ID: {}", prodId);
        } catch (Exception e) {
            span.setAttribute("operation.success", false);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error deleting product with ID: {}", prodId, e);
            throw e;
        } finally {
            span.end();
        }
    }
    
    // Partial update for PATCH - only updates non-null/non-zero fields
    public Product updateProductPartially(int prodId, Product updates) {
        Span span = tracer.spanBuilder("update-product-partially")
            .setAttribute("product.id", prodId)
            .setAttribute("operation.type", "database.patch")
            .startSpan();
        
        try {
            logger.info("Partially updating product with ID: {}", prodId);
            
            Product existing = getProductById(prodId);
            if (existing != null && existing.getProdId() != 0) {
                boolean hasUpdates = false;
                
                if (updates.getProdName() != null && !updates.getProdName().isEmpty()) {
                    existing.setProdName(updates.getProdName());
                    span.setAttribute("updated.name", true);
                    hasUpdates = true;
                }
                if (updates.getPrice() > 0) {
                    existing.setPrice(updates.getPrice());
                    span.setAttribute("updated.price", true);
                    hasUpdates = true;
                }
                if (updates.getCategory() != null && !updates.getCategory().isEmpty()) {
                    existing.setCategory(updates.getCategory());
                    span.setAttribute("updated.category", true);
                    hasUpdates = true;
                }
                
                if (hasUpdates) {
                    Product savedProduct = repo.save(existing);
                    span.setAttribute("operation.success", true);
                    span.setAttribute("fields.updated", hasUpdates);
                    logger.info("Successfully partially updated product with ID: {}", prodId);
                    return savedProduct;
                } else {
                    span.setAttribute("operation.success", true);
                    span.setAttribute("fields.updated", false);
                    logger.info("No updates applied to product with ID: {}", prodId);
                    return existing;
                }
            } else {
                span.setAttribute("product.found", false);
                logger.warn("Product not found for partial update with ID: {}", prodId);
                return null;
            }
        } catch (Exception e) {
            span.setAttribute("operation.success", false);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("Error partially updating product with ID: {}", prodId, e);
            throw e;
        } finally {
            span.end();
        }
    }
}
