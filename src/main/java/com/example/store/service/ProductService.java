package com.example.store.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.store.repository.ProductRepository;
import com.example.store.model.Product;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    // Likely client error
                    logger.warn(String.format("Cannot findById(%d).", id));
                    return new IllegalArgumentException("id");
                });
    }

    public List<Product> list() {
        return (List<Product>) productRepository.findAll();
    }

    public Product create(Product product) {
        logger.info(String.format("Audit Product.create %s", product.toString()));
        return productRepository.save(product);
    }

    public Product updateFull(Long id, Product product) {
        logger.info(String.format("Audit Product.updateFull %d %s", id, product.toString()));
        return productRepository.findById(id)
                .map(target -> {
                    target.setName(product.getName());
                    return productRepository.save(target);
                })
                .orElseThrow(() -> {
                    // Likely client error
                    logger.warn(String.format("Cannot findById(%d).", id));
                    return new IllegalArgumentException("id");
                });
    }

    public void deleteById(Long id) {
        logger.info(String.format("Audit Product.delete %d", id));
        productRepository.deleteById(id);
    }
}
