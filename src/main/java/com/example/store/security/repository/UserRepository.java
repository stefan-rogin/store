package com.example.store.security.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.store.security.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
