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
        product.setId(1L);
        product.setName("One");
        product.setPrice(createPriceEur(1.49));
        return product;
    }

    @Test
    void settersAndGetters() {
        Product product = new Product();
        Price price = createPriceEur(1.49);

        product.setId(1L);
        product.setName("One");
        product.setPrice(price);

        assertEquals(1L, product.getId());
        assertEquals("One", product.getName());
        assertEquals(price, product.getPrice());
    }

    @Test
    void equalsAndHashCode() {
        Product target = createProductOne();
        Product equalProduct = createProductOne();

        assertEquals(target, target);
        assertEquals(target, equalProduct);
        assertEquals(target.hashCode(), equalProduct.hashCode());
    }

    @Test
    void notEqualsAndHashCode() {
        Product target = createProductOne();

        Product differentId = createProductOne();
        differentId.setId(2L);

        Product allDifferent = new Product();
        allDifferent.setId(2L);
        allDifferent.setName("Two");
        allDifferent.setPrice(createPriceEur(2.49));

        Product differentPrice = createProductOne();
        differentPrice.setPrice(createPriceEur(2.49));

        assertNotEquals(target, differentId);
        assertNotEquals(target, allDifferent);
        assertNotEquals(target, differentPrice);
        assertNotEquals(target, null);
        assertNotEquals(target, "[1] One");

        assertNotEquals(target.hashCode(), differentId.hashCode());
        assertNotEquals(target.hashCode(), allDifferent.hashCode());
        assertNotEquals(target.hashCode(), differentPrice.hashCode());
    }

    @Test
    void equalsAndHashCodeNullIds() {
        Product target = createProductOne();
        target.setId(null);

        Product equalProduct = createProductOne();
        equalProduct.setId(null);

        // TODO: Decide if this is the expected behavior
        Product notEqualHasId = createProductOne();

        assertEquals(target, equalProduct);

        assertNotEquals(target, notEqualHasId);
        assertNotEquals(target, null);
        assertNotEquals(target, "[1] One");

        assertEquals(target.hashCode(), equalProduct.hashCode());
        assertNotEquals(target.hashCode(), notEqualHasId.hashCode());
    }

    @Test
    void testToString() {
        Product product = createProductOne();
        assertEquals("[1] One", product.toString());

        product.setId(null);
        assertEquals("[null] One", product.toString());
    }
}