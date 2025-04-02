package com.example.store.service;

import java.util.Optional;
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

    @Autowired
    private ProductRepository productRepository;

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByResId(String resId) {
        return productRepository.findByResId(resId);
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

    public Product upsert(String resId, Product product) {
        return productRepository.findByResId(resId)
                .map(target -> productRepository.save(ProductUpdater.prepareUpdate(target, product)))
                .orElseGet(() -> {
                    product.setResId(resId);
                    return productRepository.save(product);
                });
    }

    public Optional<Product> patchPrice(String resId, Price newPrice) {
        return productRepository.findByResId(resId)
                .map(target -> productRepository.save(ProductUpdater.preparePatchPrice(target, newPrice)));
    }

    public Optional<Product> patchName(String resId, Product productWithNewName) {
        return productRepository.findByResId(resId)
                .map(target -> productRepository.save(ProductUpdater.preparePatchName(target, productWithNewName)));
    }

    public void deleteByResId(String resId) {
        productRepository.deleteByResId(resId);
    }
}
