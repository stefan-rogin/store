package com.example.store.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PriceTests {

    private static final Currency EUR_CURRENCY = Currency.getInstance("EUR");

    private static BigDecimal createPriceAmount(double amount) {
        return BigDecimal.valueOf(amount).setScale(Price.DEFAULT_SCALE, Price.DEFAULT_ROUNDING_MODE);
    }

    @Test
    void constructorValid() {
        Price price1 = new Price(createPriceAmount(1.49), EUR_CURRENCY);
        Price price2 = new Price(createPriceAmount(1.495), EUR_CURRENCY);

        assertEquals(createPriceAmount(1.49), price1.getAmount());
        assertEquals("EUR", price1.getCurrency().getCurrencyCode());        
        assertEquals(createPriceAmount(1.5), price2.getAmount());
        assertEquals("EUR", price2.getCurrency().getCurrencyCode());
    }

    @Test
    void failConstructorInvalid() {
        assertThrows(IllegalArgumentException.class, 
                () -> new Price(createPriceAmount(-1), EUR_CURRENCY));
        assertThrows(IllegalArgumentException.class, 
                () -> new Price(createPriceAmount(1.49), null));
    }

    @Test
    void failSettingNegativeAmount() {
        Price target = new Price(createPriceAmount(1.49), EUR_CURRENCY);
        assertThrows(IllegalArgumentException.class, 
                () -> target.setAmount(createPriceAmount(-1.49)));
    }

    @Test
    void failSettingNullCurrency() {
        assertThrows(IllegalArgumentException.class, 
                () -> new Price(createPriceAmount(1.49), null));
    }

    @Test
    void equalsAndHashCode() {
        Price price1 = new Price(createPriceAmount(1.49), EUR_CURRENCY);
        Price price2 = new Price(createPriceAmount(1.49), EUR_CURRENCY);
        Price price3 = new Price(createPriceAmount(2.49), EUR_CURRENCY);
        Price price4 = new Price(createPriceAmount(1.49), Currency.getInstance("RON"));

        assertEquals(price1, price2);
        assertEquals(price1, price1);
        assertNotEquals(price1, price3);
        assertNotEquals(price1, price4);
        assertNotEquals(price1, "EUR 1.49");
        assertNotEquals(price1, null);
        assertEquals(price2.hashCode(), price1.hashCode());
        assertNotEquals(price3.hashCode(), price1.hashCode());
    }

    @Test
    void testToString() {
        Price price = new Price(createPriceAmount(1), EUR_CURRENCY);

        assertEquals("EUR 1.00", price.toString());
    }
}