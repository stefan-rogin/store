package com.example.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import com.example.store.service.ProductService;
import com.example.store.model.Product;
import com.example.store.dto.PaginatedResponse;
import com.example.store.model.Price;

@RestController
@Validated
public class ProductController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductService productService;

    // TODO: Extract PaginatedResponse
    @GetMapping("/products")
    public PaginatedResponse<Product> list(
            @PageableDefault(size = PAGE_SIZE, sort = "name") Pageable pageable) {

        Page<Product> products = productService.list(pageable);

        PaginatedResponse<Product> response = new PaginatedResponse<>();
        response.setContent(products.getContent());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        return response;
    }

    @GetMapping("/products/{id}")
    public Product findById(@PathVariable UUID id) {
        return productService.findById(id)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/products/search")
    public PaginatedResponse<Product> search(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
                
        Page<Product> products = productService.search(searchTerm, pageable);
        PaginatedResponse<Product> response = new PaginatedResponse<>();
        response.setContent(products.getContent());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        return response;
    }

    @PostMapping("/products")
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/products/{id}")
    public Product upsert(@PathVariable UUID id, @Valid @RequestBody Product product, HttpServletRequest request) {
        return productService.upsert(id, product);
    }

    @PatchMapping("/products/{id}/price")
    public Product patchPrice(@PathVariable UUID id, @Valid @RequestBody Price newPrice, HttpServletRequest request) {
        return productService.patchPrice(id, newPrice)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/products/{id}/name")
    public Product patchName(@PathVariable UUID id, @Valid @RequestBody Product productWithNewName, HttpServletRequest request) {
        return productService.patchName(id, productWithNewName)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
        
    @DeleteMapping("/products/{id}")
    public void deleteById(@PathVariable UUID id) {
        productService.deleteById(id);
    }

}
