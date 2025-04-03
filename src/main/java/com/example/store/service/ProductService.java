package com.example.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.store.repository.ProductRepository;
import com.example.store.model.Product;
import com.example.store.model.Price;
import com.example.store.util.ProductUpdater;

/**
 * Service class for products. The most meaningful implementation is for PUT,
 * where it either creates or updates a fiven resource, depending if it's already
 * present in the database or not. Empty optionals are handled by the controller.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Get a specific product
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    // Lists products by page
    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Searches by name fragment and responds by page
    public Page<Product> search(String searchTerm, Pageable pageable) {
        return productRepository.search(searchTerm, pageable);
    }

    // Create a new product (POST use case)
    public Product create(Product product) {
        return productRepository.save(product);
    }

    // Create or update a product (PUT use case)
    public Product upsert(UUID id, Product product) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.prepareUpdate(target, product)))
                .orElseGet(() -> {
                    product.setId(id);
                    return productRepository.save(product);
                });
    }

    // Change price
    public Optional<Product> patchPrice(UUID id, Price newPrice) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchPrice(target, newPrice)));
    }

    // Change name
    public Optional<Product> patchName(UUID id, Product productWithNewName) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchName(target, productWithNewName)));
    }

    // Delete product
    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }
}
