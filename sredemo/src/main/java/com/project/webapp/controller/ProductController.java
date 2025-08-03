package com.project.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// OpenTelemetry imports for custom controller telemetry
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.LongCounter;

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;

@RestController
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductService service;
    
    // OpenTelemetry setup for controller-level metrics
    private final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
    private final Tracer tracer = openTelemetry.getTracer("azure-sre-demo-controller");
    private final Meter meter = openTelemetry.getMeter("azure-sre-demo-controller");
    
    // Custom HTTP request metrics
    private final LongCounter httpRequestCounter = meter
        .counterBuilder("http.requests.total")
        .setDescription("Total number of HTTP requests")
        .build();
    
    private final LongCounter httpErrorCounter = meter
        .counterBuilder("http.errors.total")
        .setDescription("Total number of HTTP errors")
        .build();

    @RequestMapping("/products")
    public ResponseEntity<List<Product>> getProduct() {
        Span span = tracer.spanBuilder("GET /products")
            .setAttribute("http.method", "GET")
            .setAttribute("http.route", "/products")
            .startSpan();
        
        try {
            logger.info("GET /products - Retrieving all products");
            httpRequestCounter.add(1);
            
            List<Product> products = service.getProduct();
            
            span.setAttribute("http.status_code", 200);
            span.setAttribute("products.count", products.size());
            span.setStatus(StatusCode.OK);
            
            logger.info("GET /products - Successfully returned {} products", products.size());
            return ResponseEntity.ok(products);
            
        } catch (Exception e) {
            httpErrorCounter.add(1);
            span.setAttribute("http.status_code", 500);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("GET /products - Error retrieving products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            span.end();
        }
    }

    @RequestMapping("/products/{prodId}")
    public ResponseEntity<Product> getProductById(@PathVariable int prodId) {
        Span span = tracer.spanBuilder("GET /products/{id}")
            .setAttribute("http.method", "GET")
            .setAttribute("http.route", "/products/{id}")
            .setAttribute("product.id", prodId)
            .startSpan();
        
        try {
            logger.info("GET /products/{} - Retrieving product by ID", prodId);
            httpRequestCounter.add(1);
            
            Product product = service.getProductById(prodId);
            
            if (product.getProdId() != 0) {
                span.setAttribute("http.status_code", 200);
                span.setAttribute("product.found", true);
                span.setStatus(StatusCode.OK);
                logger.info("GET /products/{} - Product found: {}", prodId, product.getProdName());
                return ResponseEntity.ok(product);
            } else {
                span.setAttribute("http.status_code", 404);
                span.setAttribute("product.found", false);
                span.setStatus(StatusCode.OK);
                logger.warn("GET /products/{} - Product not found", prodId);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            httpErrorCounter.add(1);
            span.setAttribute("http.status_code", 500);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            logger.error("GET /products/{} - Error retrieving product", prodId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            span.end();
        }
    }
    }

    // using @PostMapping for post operation as @RequestMapping is get by default
    @PostMapping("/products")
    public String addProduct(@RequestBody Product prod) {
        service.addProduct(prod);
        return "Product added Successfully";
    }
    @PutMapping("/products/{prodId}")
    public String updateProduct(@PathVariable int prodId, @RequestBody Product prod){
        service.updateProduct(prodId, prod);
        return "Product updated Successfully!";

    }
    @DeleteMapping("/products/{prodId}")
    public String deleteProduct(@PathVariable int prodId){
        service.deleteProduct(prodId);
        return "Product deleted Successfully!";
    }
    //do patch mapping to update single product (partial update)
    @PatchMapping("/products/{prodId}")
    public String updateProductPartially(@PathVariable int prodId, @RequestBody Product prod) {
        service.updateProductPartially(prodId, prod);
        return "Product updated Successfully!";
    }

}
