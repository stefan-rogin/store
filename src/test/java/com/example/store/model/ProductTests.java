package com.example.store.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;


class ProductTests {

    private Price createPriceEur(double amount) {
        return new Price(BigDecimal.valueOf(amount), Currency.getInstance("EUR"));
    }

    @Test
    void constructorValid() {
        Price price1 = createPriceEur(1.49);
        Price price2 = createPriceEur(2);
        Product product1 = new Product(1L, "One", price1);
        Product product2 = new Product(2L, "Two", price2);

        assertEquals(1L, product1.getId());
        assertEquals("One", product1.getName());
        assertEquals(price1, product1.getPrice());
        assertEquals(2L, product2.getId());
        assertEquals("Two", product2.getName());
        assertEquals(price2, product2.getPrice());      
    }

    @Test
    void constructorPartialValid() {
        Price price = createPriceEur(1.49);
        Product product = new Product("One", price);

        assertNull(product.getId());
        assertEquals("One", product.getName());
        assertEquals(price, product.getPrice());
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
        Price price1 = createPriceEur(1.49);
        Price price2 = createPriceEur(2.49);
        Product product1 = new Product(1L, "One", price1);
        Product product2 = new Product(1L, "One", price1);
        Product product3ne = new Product(2L, "One", price1);
        Product product4ne = new Product(2L, "Two", price2);
        Product product5ne = new Product(1L, "One", price2);

        assertEquals(product1, product1);
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());

        assertNotEquals(product1, product3ne);
        assertNotEquals(product1, product4ne);
        assertNotEquals(product1, product5ne);
        assertNotEquals(product1, null);
        assertNotEquals(product1, "[1] One");
        assertNotEquals(product1.hashCode(), product3ne.hashCode());
        assertNotEquals(product1.hashCode(), product4ne.hashCode());
        assertNotEquals(product1.hashCode(), product5ne.hashCode());
    }

    @Test
    void equalsAndHashCodeNullIds() {
        Price price = createPriceEur(1.49);
        Product product1 = new Product("One", price);
        Product product2 = new Product("One", price);
        Product product3 = new Product(1L, "One", price);

        assertEquals(product1, product2);
        
        assertNotEquals(product1, product3);
        assertNotEquals(product1, null);
        assertNotEquals(product1, "[1] One");
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testToString() {
        Product product = new Product(1L, "One", createPriceEur(1.49));
        assertEquals("[1] One", product.toString());
        product.setId(null);
        assertEquals("[null] One", product.toString());
    }
}