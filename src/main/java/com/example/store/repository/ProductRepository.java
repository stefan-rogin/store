package com.example.store.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.store.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
