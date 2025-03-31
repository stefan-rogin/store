package com.example.store.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
                .map(target -> productRepository.save(prepareUpdate(target, product))) // TODO: If prepare throws
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));
    }

    public Product patch(Long id, Map<String, Object> updates) {
        // TODO: Deserialize updates for log
        logger.info(String.format("Audit Product.patch %d", id));
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product resource not found for id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    product.setName((String) value);
                    break;
                case "price":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> priceMap = value instanceof Map ? (Map<String, Object>) value : null;
                    if (priceMap == null
                            || !priceMap.containsKey("amount")
                            || !priceMap.containsKey("currency")) {
                        throw new IllegalArgumentException("Invalid field: price");
                    }
                    BigDecimal amount = new BigDecimal(priceMap.get("amount").toString());
                    Currency currency = Currency.getInstance(priceMap.get("currency").toString());
                    if (amount == null || currency == null) {
                        throw new IllegalArgumentException("Invalid field: price");
                    }
                    product.getPrice().setAmount(amount);
                    product.getPrice().setCurrency(currency);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        logger.info(String.format("Audit Product.delete %d", id));
        productRepository.deleteById(id);
    }

    public Product prepareUpdate(Product product, Product update) {
        // TODO: Actual implementation for price change
        product.setName(update.getName());
        product.setPrice(update.getPrice());
        return product;
    }
}
