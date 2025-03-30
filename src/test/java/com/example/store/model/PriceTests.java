package com.example.store.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceTests {

    private static final Currency EUR_CURRENCY = Currency.getInstance("EUR");

    @Test
    void constructorValid() {
        Price price = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);

        assertEquals(BigDecimal.valueOf(1.49), price.getAmount());
        assertEquals("EUR", price.getCurrency().getCurrencyCode());
    }

    @Test
    void failConstructorNegativeAmount() {
        assertThrows(IllegalArgumentException.class, 
                () -> new Price(BigDecimal.valueOf(-1), EUR_CURRENCY));
    }

    @Test
    void addValid() {
        Price price1 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price2 = new Price(BigDecimal.valueOf(2.49), EUR_CURRENCY);

        Price result = price1.add(price2);

        assertEquals(BigDecimal.valueOf(3.98), result.getAmount());
        assertEquals("EUR", result.getCurrency().getCurrencyCode());
    }

    @Test
    void failAddDifferentCurrency() {
        Price price1 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price2 = new Price(BigDecimal.valueOf(2.49), Currency.getInstance("RON"));

        assertThrows(IllegalArgumentException.class, () -> price1.add(price2));
    }

    @Test
    void subtractValid() {
        Price price1 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price2 = new Price(BigDecimal.valueOf(2.49), EUR_CURRENCY);

        Price result = price2.subtract(price1);

        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP), result.getAmount());
        assertEquals("EUR", result.getCurrency().getCurrencyCode());
    }

    @Test
    void failSubtractDifferentCurrency() {
        Price price1 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price2 = new Price(BigDecimal.valueOf(2.49), Currency.getInstance("RON"));

        assertThrows(IllegalArgumentException.class, () -> price2.subtract(price1));
    }

    @Test
    void equalsAndHashCode() {
        Price price1 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price2 = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);
        Price price3 = new Price(BigDecimal.valueOf(2.49), EUR_CURRENCY);
        String price4 = "EUR 1.49";
        Price price5 = price1;

        assertEquals(price1, price2);
        assertEquals(price1, price5);
        assertNotEquals(price1, price3);
        assertNotEquals(price1, price4);
        assertNotEquals(price1, null);
        assertEquals(price1.hashCode(), price2.hashCode());
        assertNotEquals(price1.hashCode(), price3.hashCode());
    }

    @Test
    void testToString() {
        Price price = new Price(BigDecimal.valueOf(1.49), EUR_CURRENCY);

        assertEquals("EUR 1.49", price.toString());
    }
}