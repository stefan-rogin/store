package com.example.store.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.example.store.repository.ProductRepository;
import com.example.store.model.Product;
import com.example.store.model.Price;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    public List<Product> list() {
        return (List<Product>) productRepository.findAll();
    }

    public Product create(Product product) {
        logger.info(String.format("Audit Product.create %s", product.toString()));
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        logger.info(String.format("Audit Product.update %d %s", id, product.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(prepareUpdate(target, product))) 
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    public Product patchPrice(Long id, Price newPrice) {
        logger.info(String.format("Audit Product.patch.price %d %s", id, newPrice.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(preparePatchPrice(target, newPrice))) 
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    public Product patchName(Long id, Product productWithNewName) {
        logger.info(String.format("Audit Product.patch.name %d %s", id, productWithNewName.toString()));
        return productRepository.findById(id)
                .map(target -> productRepository.save(preparePatchName(target, productWithNewName))) 
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
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
