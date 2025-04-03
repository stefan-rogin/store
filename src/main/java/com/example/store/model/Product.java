package com.example.store.model;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.SoftDelete;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@SoftDelete
public class Product implements Identifiable<UUID> {

    public static final int MAX_NAME_SIZE = 2000;

    @Id
    private UUID id = UUID.randomUUID();

    @NotBlank
    @Size(min = 1, max = MAX_NAME_SIZE)
    @Column(columnDefinition = "TEXT")
    private String name;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Price price;

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
        return String.format("{[%s][%s][%s]}", id, name, price);
    }

}
