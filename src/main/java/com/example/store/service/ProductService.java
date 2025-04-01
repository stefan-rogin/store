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
        logger.info(String.format("Audit Product.create %s", product.toString()));
        return productRepository.save(product);
    }

    public Optional<Product> update(Long id, Product product) {
        logger.info(String.format("Audit Product.update %d %s", id, product.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(prepareUpdate(target, product)));
    }

    public Optional<Product> patchPrice(Long id, Price newPrice) {
        logger.info(String.format("Audit Product.patch.price %d %s", id, newPrice.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(preparePatchPrice(target, newPrice)));
    }

    public Optional<Product> patchName(Long id, Product productWithNewName) {
        logger.info(String.format("Audit Product.patch.name %d %s", id, productWithNewName.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(preparePatchName(target, productWithNewName)));
    }

    public void deleteById(Long id) {
        logger.info(String.format("Audit Product.delete %d", id));
        productRepository.deleteById(id);
    }

    // TODO: Extract
    public Product prepareUpdate(Product target, Product update) {
        target.setName(update.getName());
        target.setPrice(update.getPrice());
        return target;
    }

    public Product preparePatchPrice(Product target, Price newPrice) {
        target.setPrice(newPrice);
        return target;
    }

    public Product preparePatchName(Product product, Product productWithNewName) {
        product.setName(productWithNewName.getName());
        return product;
    }
}
