package com.example.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.store.model.Product;

/**
 * Repository class for products.
 */
public interface ProductRepository extends CrudRepository<Product, UUID> {
    
    // Given the simplicity of the model, a more realistic text search approach was considered out of scope.
    // Only the name is searchable, while Pageable allows pagination and flexibility for clients
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :searchTerm, '%'))")
    Page<Product> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Pageable allows pagination and flexibility for clients
    Page<Product> findAll(Pageable pageable);

}
