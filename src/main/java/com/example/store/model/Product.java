package com.example.store.model;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.SoftDelete;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity class for products. Declares a OneToOne relation with Price.
 * For PUT's idempotence, it's helpful for the entity Id to be assignable,
 * leaving UUID as the most viable solution. Records are not erased, but
 * marked as deleted(inactive) through @SoftDelete.
 */
@Entity
@SoftDelete
public class Product implements Identifiable<UUID> {

    // Max permitted size for product name
    public static final int MAX_NAME_SIZE = 2000;

    // UUID identifier, auto-generated but assignable
    @Id
    private UUID id = UUID.randomUUID();

    // Product name
    @NotBlank
    @Size(min = 1, max = MAX_NAME_SIZE)
    @Column(columnDefinition = "TEXT")
    private String name;

    // Relation to Price. 
    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Price price;

    // Setters and getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    // Id is never null under normal conditions
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Product))
            return false;
        Product product = (Product) o;
        return Objects.equals(id, product.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s][%s][%s]", id, name, price);
    }

}
