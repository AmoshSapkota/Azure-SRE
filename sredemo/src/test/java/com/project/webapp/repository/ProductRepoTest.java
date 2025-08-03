package com.project.webapp.repository;

import com.project.webapp.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepo JPA Repository
 * Uses in-memory H2 database for testing
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
class ProductRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepo productRepo;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Laptop", 1200, "Electronics");
    }

    @Test
    @DisplayName("Should save and retrieve product")
    void testSaveAndFindProduct() {
        // Save product
        Product savedProduct = productRepo.save(testProduct);
        
        assertNotNull(savedProduct);
        assertEquals(testProduct.getProdId(), savedProduct.getProdId());
        assertEquals(testProduct.getProdName(), savedProduct.getProdName());
        assertEquals(testProduct.getPrice(), savedProduct.getPrice());
        assertEquals(testProduct.getCategory(), savedProduct.getCategory());
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindById() {
        // Given
        entityManager.persistAndFlush(testProduct);
        
        // When
        Optional<Product> found = productRepo.findById(testProduct.getProdId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals(testProduct.getProdName(), found.get().getProdName());
        assertEquals(testProduct.getPrice(), found.get().getPrice());
        assertEquals(testProduct.getCategory(), found.get().getCategory());
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void testFindByIdNotFound() {
        Optional<Product> found = productRepo.findById(999);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        // Given
        Product product1 = new Product(1, "Laptop", 1200, "Electronics");
        Product product2 = new Product(2, "Mouse", 25, "Electronics");
        Product product3 = new Product(3, "Book", 15, "Education");
        
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(product3);
        
        // When
        List<Product> products = productRepo.findAll();
        
        // Then
        assertEquals(3, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getProdName().equals("Laptop")));
        assertTrue(products.stream().anyMatch(p -> p.getProdName().equals("Mouse")));
        assertTrue(products.stream().anyMatch(p -> p.getProdName().equals("Book")));
    }

    @Test
    @DisplayName("Should delete product by ID")
    void testDeleteById() {
        // Given
        entityManager.persistAndFlush(testProduct);
        assertTrue(productRepo.findById(testProduct.getProdId()).isPresent());
        
        // When
        productRepo.deleteById(testProduct.getProdId());
        
        // Then
        assertFalse(productRepo.findById(testProduct.getProdId()).isPresent());
    }

    @Test
    @DisplayName("Should delete product entity")
    void testDeleteProduct() {
        // Given
        Product saved = entityManager.persistAndFlush(testProduct);
        assertTrue(productRepo.findById(saved.getProdId()).isPresent());
        
        // When
        productRepo.delete(saved);
        
        // Then
        assertFalse(productRepo.findById(saved.getProdId()).isPresent());
    }

    @Test
    @DisplayName("Should count products")
    void testCount() {
        // Given
        entityManager.persistAndFlush(new Product(1, "Product1", 100, "Cat1"));
        entityManager.persistAndFlush(new Product(2, "Product2", 200, "Cat2"));
        
        // When
        long count = productRepo.count();
        
        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void testExistsById() {
        // Given
        entityManager.persistAndFlush(testProduct);
        
        // When & Then
        assertTrue(productRepo.existsById(testProduct.getProdId()));
        assertFalse(productRepo.existsById(999));
    }

    @Test
    @DisplayName("Should save multiple products")
    void testSaveAll() {
        // Given
        List<Product> products = List.of(
            new Product(1, "Product1", 100, "Category1"),
            new Product(2, "Product2", 200, "Category2"),
            new Product(3, "Product3", 300, "Category3")
        );
        
        // When
        List<Product> savedProducts = productRepo.saveAll(products);
        
        // Then
        assertEquals(3, savedProducts.size());
        assertEquals(3, productRepo.count());
    }

    @Test
    @DisplayName("Should update existing product")
    void testUpdateProduct() {
        // Given
        Product saved = entityManager.persistAndFlush(testProduct);
        
        // When
        saved.setProdName("Updated Laptop");
        saved.setPrice(1500);
        Product updated = productRepo.save(saved);
        
        // Then
        assertEquals("Updated Laptop", updated.getProdName());
        assertEquals(1500, updated.getPrice());
        
        // Verify in database
        Optional<Product> found = productRepo.findById(saved.getProdId());
        assertTrue(found.isPresent());
        assertEquals("Updated Laptop", found.get().getProdName());
        assertEquals(1500, found.get().getPrice());
    }

    @Test
    @DisplayName("Should handle empty repository")
    void testEmptyRepository() {
        List<Product> products = productRepo.findAll();
        assertTrue(products.isEmpty());
        assertEquals(0, productRepo.count());
    }
}
