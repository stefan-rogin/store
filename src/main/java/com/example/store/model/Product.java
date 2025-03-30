package com.example.store.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Price price;

    public Product() {

    }

    public Product(String name, Price price) {
        this.name = name;
        this.price = price;
    }

    public Product(Long id, String name, Price price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

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
