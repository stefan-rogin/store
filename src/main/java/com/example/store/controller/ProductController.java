package com.example.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import com.example.store.service.ProductService;
import com.example.store.model.Product;
import com.example.store.model.Price;

@RestController
@Validated
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> list() {
        return productService.list();
    }

    @GetMapping("/products/{id}")
    public Product getById(@Positive @PathVariable Long id) {
        return productService.getById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }
    
    @PostMapping("/products")
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/products/{id}")
    public Product update(@Positive @PathVariable Long id, @Valid @RequestBody Product product) {
        return productService.update(id, product)
            .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    @PatchMapping("/products/{id}/price")
    public Product patchPrice(@Positive @PathVariable Long id, @Valid @RequestBody Price newPrice) {
        return productService.patchPrice(id, newPrice)
            .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    @PatchMapping("/products/{id}/name")
    public Product patch(@Positive @PathVariable Long id, @Valid @RequestBody Product productWithNewName) {
        return productService.patchName(id, productWithNewName)
            .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }
        
    @DeleteMapping("/products/{id}")
    public void deleteById(@Positive @PathVariable Long id) {
        productService.deleteById(id);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        // Likely client error
        logger.warn(ex.getMessage());
        return new ResponseEntity<>(detail, HttpStatus.NOT_FOUND);
    }

}
