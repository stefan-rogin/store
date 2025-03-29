package com.example.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.store.repository.ProductRepository;
import com.example.store.model.Product;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unable to find product having Id:%d", id)));
    }

    public List<Product> list() {
        return (List<Product>) productRepository.findAll();
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product updateFull(Long id, Product product) {
        return productRepository.findById(id)
                .map(target -> {
                    target.setName(product.getName());
                    return productRepository.save(target);
                })
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unable to find product having Id:%d", id)));
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
