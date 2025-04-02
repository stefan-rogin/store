package com.example.store.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceTests {

    private static BigDecimal createPriceAmount(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE);
    }

    private static Price createPriceEur(double amount) {
        Price price = new Price();
        price.setAmount(createPriceAmount(amount));
        price.setCurrency(Currency.getInstance("EUR"));
        return price;
    }

    @Test
    void settersAndGetters() {
        Price regular = createPriceEur(1.49);
        Price rounded = createPriceEur(1.495);

        assertEquals(createPriceAmount(1.49), regular.getAmount());
        assertEquals("EUR", regular.getCurrency().getCurrencyCode());
        assertEquals(createPriceAmount(1.5), rounded.getAmount());
        assertEquals("EUR", rounded.getCurrency().getCurrencyCode());
    }

    void equalsAndHashCode() {
        Price target = createPriceEur(1.49);
        Price equalPrice = createPriceEur(1.49);

        assertEquals(target, equalPrice);
        assertEquals(target, target);
        assertEquals(target.hashCode(), equalPrice.hashCode());
    }

    @Test
    void notEqualsAndHashCode() {
        Price target = createPriceEur(1.49);
        Price differentPrice = createPriceEur(2.49);
        Price otherCurrency = createPriceEur(1.49);
        otherCurrency.setCurrency(Currency.getInstance("RON"));

        assertEquals(target, target);
        assertNotEquals(target, differentPrice);
        assertNotEquals(target, otherCurrency);
        assertNotEquals(target, "EUR 1.49");
        assertNotEquals(target, null);
        assertNotEquals(target.hashCode(), differentPrice.hashCode());
        assertNotEquals(target.hashCode(), otherCurrency.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("EUR 1.00", createPriceEur(1.00).toString());
    }
}