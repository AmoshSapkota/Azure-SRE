package com.project.webapp.controller;

import com.project.webapp.service.TelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate custom telemetry capabilities
 */
@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {
    
    @Autowired
    private TelemetryService telemetryService;
    
    /**
     * Endpoint to test custom telemetry
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testTelemetry() {
        return telemetryService.executeWithSpan("telemetry.test", span -> {
            telemetryService.addSpanAttributes("test.type", "custom_telemetry_demo");
            telemetryService.addSpanAttributes("endpoint", "/api/telemetry/test");
            
            // Record custom metrics
            telemetryService.recordProductOperation("test", "telemetry", "success");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Custom telemetry test completed");
            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            return ResponseEntity.ok(response);
        });
    }
    
    /**
     * Endpoint to simulate various telemetry scenarios
     */
    @GetMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulateTelemetry() {
        return telemetryService.executeWithSpan("telemetry.simulate", span -> {
            telemetryService.addSpanAttributes("simulation.type", "telemetry_scenarios");
            
            Map<String, Object> results = new HashMap<>();
            
            // Simulate various operations
            simulateProductOperations();
            simulateDatabaseOperations();
            simulateBusinessMetrics();
            
            results.put("status", "completed");
            results.put("simulations", "product_ops,database_ops,business_metrics");
            results.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(results);
        });
    }
    
    private void simulateProductOperations() {
        telemetryService.executeWithSpan("simulate.product_operations", span -> {
            // Simulate different product operations
            telemetryService.recordProductOperation("add", "Electronics", "success");
            telemetryService.recordProductOperation("add", "Books", "success");
            telemetryService.recordProductOperation("update", "Electronics", "success");
            telemetryService.recordProductOperation("delete", "Books", "success");
            
            telemetryService.addSpanAttributes("simulated.operations", "4");
            return null;
        });
    }
    
    private void simulateDatabaseOperations() {
        telemetryService.executeWithSpan("simulate.database_operations", span -> {
            // Simulate various database operations
            telemetryService.recordDatabaseOperation("select", "success");
            telemetryService.recordDatabaseOperation("insert", "success");
            telemetryService.recordDatabaseOperation("update", "success");
            telemetryService.recordDatabaseOperation("delete", "success");
            
            telemetryService.addSpanAttributes("simulated.db_operations", "4");
            return null;
        });
    }
    
    private void simulateBusinessMetrics() {
        telemetryService.executeWithSpan("simulate.business_metrics", span -> {
            // Simulate product price distributions
            telemetryService.recordProductPrice(299.99, "Electronics");
            telemetryService.recordProductPrice(19.99, "Books");
            telemetryService.recordProductPrice(599.99, "Electronics");
            telemetryService.recordProductPrice(29.99, "Books");
            telemetryService.recordProductPrice(1299.99, "Electronics");
            
            telemetryService.addSpanAttributes("simulated.price_points", "5");
            return null;
        });
    }
}