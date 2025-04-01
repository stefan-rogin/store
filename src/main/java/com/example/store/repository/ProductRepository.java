package com.example.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.store.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
    
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :searchTerm, '%'))")
    Page<Product> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
}
