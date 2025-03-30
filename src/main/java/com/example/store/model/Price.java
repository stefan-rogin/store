package com.example.store.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal amount;
    private Currency currency;

    public Price() {
        
    }

    public Price(BigDecimal amount, Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot create a negative price.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Price add(Price augend) {
        if (!currency.equals(augend.getCurrency())) {
            throw new IllegalArgumentException("Cannot operate different currencies.");
        }
        return new Price(amount.add(augend.getAmount()).setScale(2, RoundingMode.HALF_UP), currency);
    }

    public Price subtract(Price augend) {
        if (!currency.equals(augend.getCurrency())) {
            throw new IllegalArgumentException("Cannot operate different currencies.");
        }
        return new Price(amount.subtract(augend.getAmount()).setScale(2, RoundingMode.HALF_UP), currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return amount.equals(price.amount) && currency.equals(price.currency);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency, amount);
    }

}
