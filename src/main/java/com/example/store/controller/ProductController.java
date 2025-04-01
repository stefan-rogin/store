package com.example.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import com.example.store.service.ProductService;
import com.example.store.model.Product;
import com.example.store.model.Price;

@RestController
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public Page<Product> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return productService.list(pageable);
    }

    @GetMapping("/products/{id}")
    public Product getById(@Positive @PathVariable Long id) {
        return productService.getById(id)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/products/search")
    public Page<Product> search(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return productService.search(searchTerm, pageable);
    }

    @PostMapping("/products")
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/products/{id}")
    public Product update(@Positive @PathVariable Long id, @Valid @RequestBody Product product, HttpServletRequest request) {
        return productService.update(id, product)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/products/{id}/price")
    public Product patchPrice(@Positive @PathVariable Long id, @Valid @RequestBody Price newPrice, HttpServletRequest request) {
        return productService.patchPrice(id, newPrice)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/products/{id}/name")
    public Product patch(@Positive @PathVariable Long id, @Valid @RequestBody Product productWithNewName, HttpServletRequest request) {
        return productService.patchName(id, productWithNewName)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
        
    @DeleteMapping("/products/{id}")
    public void deleteById(@Positive @PathVariable Long id) {
        productService.deleteById(id);
    }

}
