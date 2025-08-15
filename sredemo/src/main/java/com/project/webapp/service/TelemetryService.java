package com.project.webapp.service;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Custom telemetry service using OpenTelemetry SDK
 * Provides custom metrics and tracing capabilities
 */
@Service
public class TelemetryService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);
    
    // Attribute keys
    private static final AttributeKey<String> OPERATION_KEY = AttributeKey.stringKey("operation");
    private static final AttributeKey<String> CATEGORY_KEY = AttributeKey.stringKey("category");
    private static final AttributeKey<String> STATUS_KEY = AttributeKey.stringKey("status");
    private static final AttributeKey<Long> PRODUCT_ID_KEY = AttributeKey.longKey("product.id");
    private static final AttributeKey<Double> PRICE_KEY = AttributeKey.doubleKey("product.price");
    
    @Autowired
    private Tracer tracer;
    
    @Autowired
    private Meter meter;
    
    // Custom metrics
    private LongCounter productOperationsCounter;
    private LongHistogram productPriceHistogram;
    private LongCounter databaseOperationsCounter;
    
    @PostConstruct
    public void initializeMetrics() {
        // Counter for product operations
        productOperationsCounter = meter
            .counterBuilder("product_operations_total")
            .setDescription("Total number of product operations")
            .setUnit("operations")
            .build();
            
        // Histogram for product prices
        productPriceHistogram = meter
            .histogramBuilder("product_price_distribution")
            .setDescription("Distribution of product prices")
            .setUnit("currency")
            .ofLongs()
            .build();
            
        // Counter for database operations
        databaseOperationsCounter = meter
            .counterBuilder("database_operations_total")
            .setDescription("Total number of database operations")
            .setUnit("operations")
            .build();
            
        logger.info("Custom OpenTelemetry metrics initialized");
    }
    
    /**
     * Record a product operation
     */
    public void recordProductOperation(String operation, String category, String status) {
        productOperationsCounter.add(1, Attributes.of(
            OPERATION_KEY, operation,
            CATEGORY_KEY, category != null ? category : "unknown",
            STATUS_KEY, status
        ));
    }
    
    /**
     * Record product price for analytics
     */
    public void recordProductPrice(double price, String category) {
        productPriceHistogram.record((long) price, Attributes.of(
            CATEGORY_KEY, category != null ? category : "unknown"
        ));
    }
    
    /**
     * Record database operation
     */
    public void recordDatabaseOperation(String operation, String status) {
        databaseOperationsCounter.add(1, Attributes.of(
            OPERATION_KEY, operation,
            STATUS_KEY, status
        ));
    }
    
    /**
     * Create a custom span for business logic
     */
    public Span createSpan(String spanName) {
        return tracer.spanBuilder(spanName)
            .startSpan();
    }
    
    /**
     * Execute code within a custom span
     */
    public <T> T executeWithSpan(String spanName, SpanOperation<T> operation) {
        Span span = createSpan(spanName);
        try (Scope scope = span.makeCurrent()) {
            T result = operation.execute(span);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
            return result;
        } catch (RuntimeException e) {
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } catch (Exception e) {
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw new RuntimeException(e);
        } finally {
            span.end();
        }
    }
    
    /**
     * Add custom attributes to current span
     */
    public void addSpanAttributes(String key, String value) {
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            currentSpan.setAttribute(key, value);
        }
    }
    
    /**
     * Add product-specific attributes to span
     */
    public void addProductAttributes(Long productId, String name, Double price, String category) {
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            if (productId != null) currentSpan.setAttribute(PRODUCT_ID_KEY, productId);
            if (name != null) currentSpan.setAttribute("product.name", name);
            if (price != null) currentSpan.setAttribute(PRICE_KEY, price);
            if (category != null) currentSpan.setAttribute(CATEGORY_KEY, category);
        }
    }
    
    @FunctionalInterface
    public interface SpanOperation<T> {
        T execute(Span span) throws Exception;
    }
}