package com.example.store.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.store.model.Product;
import com.example.store.model.Price;

public class PaginatedResponseTest {

    private static Product createTestProduct() {
        Price price = new Price();
        price.setAmount(BigDecimal.valueOf(1.49));
        price.setCurrency(Currency.getInstance("EUR"));
        Product product = new Product();
        product.setId(UUID.fromString("33b5785c-8d8a-4301-b5b3-b07b67347173"));
        product.setName("One");
        product.setPrice(price);

        return product;
    }

    @Test
    void mapsPagetoPaginatedResponse() throws Exception {
        Page<Product> products = new PageImpl<Product>(List.of(
                createTestProduct(),
                createTestProduct(),
                createTestProduct()));
        PaginatedResponse<Product> response = PaginatedResponse.of(products);
        assertEquals(0, response.getPageNumber());
        assertEquals(3, response.getPageSize());
        assertEquals(3, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(3, response.getContent().size());
    }

}
