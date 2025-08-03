package com.project.webapp.controller;

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for ProductController
 * Uses MockMvc for web layer testing with mocked service layer
 * Tests REST API endpoints and HTTP responses
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Laptop", 1200, "Electronics");
        testProducts = Arrays.asList(
            new Product(1, "Laptop", 1200, "Electronics"),
            new Product(2, "Mouse", 25, "Electronics"),
            new Product(3, "Book", 15, "Education")
        );
    }

    @Test
    @DisplayName("Should return all products with HTTP 200")
    void testGetAllProducts() throws Exception {
        // Given
        when(productService.getProduct()).thenReturn(testProducts);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].prodName", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(1200)))
                .andExpect(jsonPath("$[0].category", is("Electronics")))
                .andExpect(jsonPath("$[1].prodName", is("Mouse")))
                .andExpect(jsonPath("$[2].prodName", is("Book")));

        verify(productService, times(1)).getProduct();
    }

    @Test
    @DisplayName("Should return empty list with HTTP 200 when no products exist")
    void testGetAllProductsEmpty() throws Exception {
        // Given
        when(productService.getProduct()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getProduct();
    }

    @Test
    @DisplayName("Should return product by ID with HTTP 200")
    void testGetProductById() throws Exception {
        // Given
        when(productService.getProductById(1)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prodId", is(1)))
                .andExpect(jsonPath("$.prodName", is("Test Laptop")))
                .andExpect(jsonPath("$.price", is(1200)))
                .andExpect(jsonPath("$.category", is("Electronics")));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    @DisplayName("Should return HTTP 404 when product not found")
    void testGetProductByIdNotFound() throws Exception {
        // Given
        Product emptyProduct = new Product(); // Default constructor creates product with ID 0
        when(productService.getProductById(999)).thenReturn(emptyProduct);

        // When & Then
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999);
    }

    @Test
    @DisplayName("Should return HTTP 404 when product service returns null")
    void testGetProductByIdReturnsNull() throws Exception {
        // Given
        when(productService.getProductById(999)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999);
    }

    @Test
    @DisplayName("Should create product and return HTTP 201")
    void testAddProduct() throws Exception {
        // Given
        doNothing().when(productService).addProduct(any(Product.class));

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prodId", is(1)))
                .andExpect(jsonPath("$.prodName", is("Test Laptop")))
                .andExpect(jsonPath("$.price", is(1200)))
                .andExpect(jsonPath("$.category", is("Electronics")));

        verify(productService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 for invalid product data")
    void testAddProductInvalidData() throws Exception {
        // Given - Product with invalid data (empty name)
        Product invalidProduct = new Product(1, "", -100, "");

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 for malformed JSON")
    void testAddProductMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should update product and return HTTP 200")
    void testUpdateProduct() throws Exception {
        // Given
        doNothing().when(productService).updateProduct(eq(1), any(Product.class));

        // When & Then
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodId", is(1)))
                .andExpect(jsonPath("$.prodName", is("Test Laptop")));

        verify(productService, times(1)).updateProduct(eq(1), any(Product.class));
    }

    @Test
    @DisplayName("Should partially update product and return HTTP 200")
    void testPatchProduct() throws Exception {
        // Given
        Product partialUpdate = new Product(0, "Updated Laptop", 1500, null);
        doNothing().when(productService).updateProductPartially(eq(1), any(Product.class));

        // When & Then
        mockMvc.perform(patch("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prodName", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1500)));

        verify(productService, times(1)).updateProductPartially(eq(1), any(Product.class));
    }

    @Test
    @DisplayName("Should delete product and return HTTP 204")
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1);
    }

    @Test
    @DisplayName("Should handle service exception with HTTP 500")
    void testServiceExceptionHandling() throws Exception {
        // Given
        when(productService.getProduct()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).getProduct();
    }

    @Test
    @DisplayName("Should handle service exception during product creation")
    void testAddProductServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Save failed")).when(productService).addProduct(any(Product.class));

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should handle service exception during product update")
    void testUpdateProductServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Update failed")).when(productService).updateProduct(eq(1), any(Product.class));

        // When & Then
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).updateProduct(eq(1), any(Product.class));
    }

    @Test
    @DisplayName("Should handle service exception during product deletion")
    void testDeleteProductServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Delete failed")).when(productService).deleteProduct(1);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).deleteProduct(1);
    }

    @Test
    @DisplayName("Should handle invalid path parameter")
    void testInvalidPathParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/products/invalid"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).getProductById(anyInt());
    }

    @Test
    @DisplayName("Should handle negative product ID")
    void testNegativeProductId() throws Exception {
        // Given
        when(productService.getProductById(-1)).thenReturn(new Product());

        // When & Then
        mockMvc.perform(get("/products/-1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(-1);
    }

    @Test
    @DisplayName("Should handle unsupported HTTP method")
    void testUnsupportedHttpMethod() throws Exception {
        // When & Then
        mockMvc.perform(patch("/products"))
                .andExpect(status().isMethodNotAllowed());

        verify(productService, never()).getProduct();
    }

    @Test
    @DisplayName("Should handle missing request body for POST")
    void testMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, never()).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should validate Content-Type header")
    void testInvalidContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isUnsupportedMediaType());

        verify(productService, never()).addProduct(any(Product.class));
    }
}
