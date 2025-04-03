package com.example.store.controller;

import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

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

    @Autowired
    private ProductService productService;

    // TODO: Extract PaginatedResponse
    @GetMapping("/products")
    public PaginatedResponse<Product> list(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<Product> products = productService.list(pageable);

        PaginatedResponse<Product> response = new PaginatedResponse<>();
        response.setContent(products.getContent());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        return response;
    }

    @GetMapping("/products/{resId}")
    public Product findResById(@UUID @PathVariable String resId) {
        return productService.findByResId(resId)
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

    @PutMapping("/products/{resId}")
    public Product upsert(@UUID @PathVariable String resId, @Valid @RequestBody Product product, HttpServletRequest request) {
        return productService.upsert(resId, product);
    }

    @PatchMapping("/products/{resId}/price")
    public Product patchPrice(@UUID @PathVariable String resId, @Valid @RequestBody Price newPrice, HttpServletRequest request) {
        return productService.patchPrice(resId, newPrice)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/products/{resId}/name")
    public Product patchName(@UUID @PathVariable String resId, @Valid @RequestBody Product productWithNewName, HttpServletRequest request) {
        return productService.patchName(resId, productWithNewName)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
        
    @DeleteMapping("/products/{resId}")
    public void deleteById(@UUID @PathVariable String resId) {
        productService.deleteByResId(resId);
    }

}
