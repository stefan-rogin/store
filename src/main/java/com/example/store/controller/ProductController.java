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
import com.example.store.log.Auditable;
import com.example.store.model.Price;

/**
 * Main controller of the service. UTF-8 is configured through application.properties.
 */
@RestController
@Validated
public class ProductController {

    // Default page size and sort criteria
    private static final int PAGE_SIZE = 10;
    private static final String PAGE_SORT = "name";

    @Autowired
    private ProductService productService;

    // List products by page
    @GetMapping("/products")
    public PaginatedResponse<Product> list(
            @PageableDefault(size = PAGE_SIZE, sort = PAGE_SORT) Pageable pageable) {

        // Get page of products
        Page<Product> products = productService.list(pageable);
        // Respond with a DTO to ensure consistency
        return PaginatedResponse.of(products);
    }

    // Get product by Id
    @GetMapping("/products/{id}")
    public Product findById(@PathVariable UUID id) {
        return productService.findById(id)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
    
    // Find product by name (price not included in search)
    // Given the purpose of the project, the search is not FULLTEXT
    @GetMapping("/products/search")
    public PaginatedResponse<Product> search(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = PAGE_SIZE, sort = PAGE_SORT) Pageable pageable) {
                
        Page<Product> products = productService.search(searchTerm, pageable);
        // Same as list(), respond through DTO
        return PaginatedResponse.of(products);
    }

    // Create new product
    // With @Auditable to trace this action in audit log, same as other DB changing actions
    @Auditable
    @PostMapping("/products")
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    // Idempotent action, creates a product with the given Id or updates if it already exists
    // Only the path Id is considered; if present in the request body, it is ignored
    @Auditable
    @PutMapping("/products/{id}")
    public Product upsert(@PathVariable UUID id, @Valid @RequestBody Product product, HttpServletRequest request) {
        return productService.upsert(id, product);
    }

    // Change the price of a product. If a name is present in body, it is ignored.
    @Auditable
    @PatchMapping("/products/{id}/price")
    public Product patchPrice(@PathVariable UUID id, @Valid @RequestBody Price newPrice, HttpServletRequest request) {
        return productService.patchPrice(id, newPrice)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }

    // Change the name of a product. If a price is present in body, it is ignored.
    @Auditable
    @PatchMapping("/products/{id}/name")
    public Product patchName(@PathVariable UUID id, @Valid @RequestBody Product productWithNewName, HttpServletRequest request) {
        return productService.patchName(id, productWithNewName)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    }
        
    // Delete a product, without confirmation if there was a resource for the Id or not. 
    @Auditable
    @DeleteMapping("/products/{id}")
    public void deleteById(@PathVariable UUID id) {
        productService.deleteById(id);
    }

}
