package com.example.store.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.hibernate.annotations.SoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@SoftDelete
public class Price {

    public static final int DEFAULT_SCALE = 2;
    public static final int DEFAULT_PRECISION = 19;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(precision = DEFAULT_PRECISION, scale = DEFAULT_SCALE)
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    public Price() {
        
    }

    public Price(BigDecimal amount, Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot create a negative price.");
        }
        this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    // TODO: Keep?
    public void setAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot create a negative price.");
        }
        this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Price add(Price augend) {
        if (!currency.equals(augend.getCurrency())) {
            throw new IllegalArgumentException("Cannot operate different currencies.");
        }
        return new Price(amount.add(augend.getAmount()).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE), currency);
    }

    public Price subtract(Price augend) {
        if (!currency.equals(augend.getCurrency())) {
            throw new IllegalArgumentException("Cannot operate different currencies.");
        }
        return new Price(amount.subtract(augend.getAmount()).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE), currency);
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
