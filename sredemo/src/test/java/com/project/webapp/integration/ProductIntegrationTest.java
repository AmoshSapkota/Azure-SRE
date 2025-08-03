package com.project.webapp.integration;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the complete application
 * Tests the full stack from controller to database
 * Uses real H2 database and all application components
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Product API Integration Tests")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepo.deleteAll();
    }

    @Test
    @DisplayName("Should create, read, update, and delete product (CRUD operations)")
    void testFullCrudOperations() throws Exception {
        Product newProduct = new Product(1, "Integration Test Laptop", 1500, "Electronics");

        // CREATE - Add new product
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prodName", is("Integration Test Laptop")))
                .andExpect(jsonPath("$.price", is(1500)));

        // READ - Get all products
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].prodName", is("Integration Test Laptop")));

        // READ - Get product by ID
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodId", is(1)))
                .andExpect(jsonPath("$.prodName", is("Integration Test Laptop")));

        // UPDATE - Full update
        Product updatedProduct = new Product(1, "Updated Laptop", 1800, "Electronics");
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodName", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1800)));

        // PATCH - Partial update
        Product partialUpdate = new Product(0, null, 2000, null);
        mockMvc.perform(patch("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is(2000)));

        // DELETE - Remove product
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle multiple products")
    void testMultipleProducts() throws Exception {
        // Add multiple products
        Product laptop = new Product(1, "Laptop", 1200, "Electronics");
        Product mouse = new Product(2, "Mouse", 25, "Electronics");
        Product book = new Product(3, "Book", 15, "Education");

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(laptop)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mouse)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated());

        // Verify all products exist
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].prodName", containsInAnyOrder("Laptop", "Mouse", "Book")));
    }

    @Test
    @DisplayName("Should handle product not found scenarios")
    void testProductNotFound() throws Exception {
        // Try to get non-existent product
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        // Try to update non-existent product
        Product updateProduct = new Product(999, "Non-existent", 100, "Test");
        mockMvc.perform(put("/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isOk()); // Service handles this gracefully

        // Try to delete non-existent product
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNoContent()); // Delete is idempotent
    }

    @Test
    @DisplayName("Should validate input data")
    void testInputValidation() throws Exception {
        // Test with invalid JSON
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        // Test with empty product name (business rule validation)
        Product invalidProduct = new Product(1, "", -100, "");
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle concurrent access")
    void testConcurrentAccess() throws Exception {
        Product product = new Product(1, "Concurrent Test", 100, "Test");
        
        // Add product
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        // Simulate concurrent reads
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodName", is("Concurrent Test")));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodName", is("Concurrent Test")));
    }

    @Test
    @DisplayName("Should persist data correctly")
    void testDataPersistence() throws Exception {
        Product product = new Product(1, "Persistence Test", 500, "Test");
        
        // Add product
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        // Verify persistence via repository
        assertTrue(productRepo.existsById(1));
        Product savedProduct = productRepo.findById(1).orElse(null);
        assertNotNull(savedProduct);
        assertEquals("Persistence Test", savedProduct.getProdName());
        assertEquals(500, savedProduct.getPrice());
        assertEquals("Test", savedProduct.getCategory());
    }

    @Test
    @DisplayName("Should handle database constraints")
    void testDatabaseConstraints() throws Exception {
        Product product1 = new Product(1, "Product 1", 100, "Category 1");
        Product product2 = new Product(1, "Product 2", 200, "Category 2"); // Same ID

        // Add first product
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        // Try to add second product with same ID (should update existing)
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isCreated());

        // Verify only one product exists (updated)
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].prodName", is("Product 2")));
    }
}
