package com.example.store.security.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.store.security.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
}