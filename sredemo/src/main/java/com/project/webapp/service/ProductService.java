package com.project.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;
 
@Service
public class ProductService {
    @Autowired
    ProductRepo repo; 
    public List<Product> getProduct() {
        return repo.findAll();
    }

    public Product getProductById(int prodId) {
        return repo.findById(prodId).orElse(new Product()); //find by id or else return empty product  
    }

    public void addProduct(Product prod) {
        repo.save(prod);
    }
    public void updateProduct(int prodId, Product prod){
        prod.setProdId(prodId);
        repo.save(prod);
    }
    public void deleteProduct(int prodId){
        repo.deleteById(prodId);
    }
    
    // Partial update for PATCH - only updates non-null/non-zero fields
    public void updateProductPartially(int prodId, Product updates) {
        Product existing = getProductById(prodId);
        if (existing != null && existing.getProdId() != 0) {
            if (updates.getProdName() != null && !updates.getProdName().isEmpty()) {
                existing.setProdName(updates.getProdName());
            }
            if (updates.getPrice() > 0) {
                existing.setPrice(updates.getPrice());
            }
            if (updates.getCategory() != null && !updates.getCategory().isEmpty()) {
                existing.setCategory(updates.getCategory());
            }
            if (updates.getImageUrl() != null && !updates.getImageUrl().isEmpty()) {
                existing.setImageUrl(updates.getImageUrl());
            }
            repo.save(existing);
        }
    }
}
