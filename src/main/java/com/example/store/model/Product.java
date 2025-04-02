package com.example.store.model;

import org.hibernate.annotations.SoftDelete;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@SoftDelete
public class Product {

    public static final int MAX_NAME_SIZE = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return (name.equals(product.name) && price.equals(product.price)) &&
                ((id == null && product.id == null) || (id != null && id.equals(product.id)));
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", id == null ? "null" : id, name);
    }

}
