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

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;

@RestController
public class ProductController {
    @Autowired
    ProductService service;

    @RequestMapping("/products")
    public List<Product> getProduct() {
        return service.getProduct();
    }

    @RequestMapping("/products/{prodId}")
    public Product getProductById(@PathVariable int prodId) {
        return service.getProductById(prodId);
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
