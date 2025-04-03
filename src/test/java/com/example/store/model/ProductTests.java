package com.example.store.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ProductTests {

    private static Price createPriceEur(double amount) {
        Price price = new Price();
        price.setAmount(BigDecimal.valueOf(amount)
                .setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE));
        price.setCurrency(Currency.getInstance("EUR"));
        return price;
    }

    private static Product createProductOne() {
        Product product = new Product();
        product.setName("One");
        product.setPrice(createPriceEur(1.49));
        return product;
    }

    @Test
    void settersAndGetters() {
        Product product = new Product();
        Price price = createPriceEur(1.49);

        product.setName("One");
        product.setPrice(price);

        assertEquals("UUID", product.getId().getClass().getSimpleName());
        assertEquals("One", product.getName());
        assertEquals(price, product.getPrice());
    }

    @Test
    void equalsAndHashCode() {
        Product target = createProductOne();
        Product equalProduct = createProductOne();
        equalProduct.setId(target.getId());

        assertEquals(target, target);
        assertEquals(target, equalProduct);
        assertEquals(target.hashCode(), equalProduct.hashCode());
    }

    @Test
    void notEqualsAndHashCode() {
        Product target = createProductOne();
        Product differentId = createProductOne();

        Product allDifferent = new Product();
        allDifferent.setName("Two");
        allDifferent.setPrice(createPriceEur(2.49));

        assertNotEquals(target, differentId);
        assertNotEquals(target, allDifferent);
        assertNotEquals(target, null);
        assertNotEquals(target, "{[33b5785c-8d8a-4301-b5b3-b07b67347173][One][{EUR 1.49}]}");

        assertNotEquals(target.hashCode(), differentId.hashCode());
        assertNotEquals(target.hashCode(), allDifferent.hashCode());
    }

    @Test
    void testToString() {
        Product product = createProductOne();
        String expectedId = product.getId().toString();
        assertEquals(String.format("{[%s][One][{EUR 1.49}]}", expectedId), product.toString());

        product.setId(null);
        assertEquals("{[null][One][{EUR 1.49}]}", product.toString());
    }
}