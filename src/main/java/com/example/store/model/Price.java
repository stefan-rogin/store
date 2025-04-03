package com.example.store.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

import org.hibernate.annotations.SoftDelete;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Entity class for prices, having defaults for scale and rounding 
 * in line with most currencies. Add() and subtract() are not implemented
 * as there was no use case for their use. Records are not erased, but
 * marked as deleted(inactive) through @SoftDelete.
 */
@Entity
@SoftDelete
public class Price implements Identifiable<Long> {

    public static final int DEFAULT_SCALE = 2;
    public static final int DEFAULT_PRECISION = 19;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    // Regular Id generation, as it donesn't need to have the Id assignable
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Currency amount enforced positive
    @Column(precision = DEFAULT_PRECISION, scale = DEFAULT_SCALE)
    @PositiveOrZero
    private BigDecimal amount;

    // The price's currency
    @NotNull
    private Currency currency;

    // Setters and getters, only setAmount having a meaningful implementation
    // equals() and hash() are standard for entities
    public BigDecimal getAmount() {
        return this.amount;
    }

    // Apply the default scale and rounding when setting amount
    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Price))
            return false;
        Price price = (Price) o;
        return id != null && id.equals(price.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s %s]", currency, amount);
    }

}
