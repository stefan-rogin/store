package com.example.store.security.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import com.example.store.security.model.User;

/**
 * Repository for User
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
