package com.example.store.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.store.repository.ProductRepository;
import com.example.store.model.Product;
import com.example.store.model.Price;
import com.example.store.util.ProductUpdater;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> search(String searchTerm, Pageable pageable) {
        return productRepository.search(searchTerm, pageable);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> update(Long id, Product product) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.prepareUpdate(target, product)));
    }

    public Optional<Product> patchPrice(Long id, Price newPrice) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchPrice(target, newPrice)));
    }

    public Optional<Product> patchName(Long id, Product productWithNewName) {
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchName(target, productWithNewName)));
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
