package com.example.store.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        logger.info(String.format(
                "Audit Product.create by user %s: %s",
                getCurrentUsername(), product.toString()));
        return productRepository.save(product);
    }

    public Optional<Product> update(Long id, Product product) {
        logger.info(String.format(
                "Audit Product.update by user %s at /%s: %s",
                getCurrentUsername(), id, product.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.prepareUpdate(target, product)));
    }

    public Optional<Product> patchPrice(Long id, Price newPrice) {
        logger.info(String.format(
                "Audit Product.patch.price by user %s at /%s: %s",
                getCurrentUsername(), id, newPrice.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchPrice(target, newPrice)));
    }

    public Optional<Product> patchName(Long id, Product productWithNewName) {
        logger.info(String.format(
                "Audit Product.patch.name by user %s at /%s: %s",
                getCurrentUsername(), id, productWithNewName.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(ProductUpdater.preparePatchName(target, productWithNewName)));
    }

    public void deleteById(Long id) {
        logger.info(String.format(
                "Audit Product.delete by user %s at /%s",
                getCurrentUsername(), id));
        productRepository.deleteById(id);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}
