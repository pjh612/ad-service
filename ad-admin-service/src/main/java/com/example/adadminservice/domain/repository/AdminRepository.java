package com.example.adadminservice.domain.repository;

import com.example.adadminservice.domain.model.Admin;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
    Optional<Admin> findById(UUID id);

    Optional<Admin> findByEmail(String email);

    Admin save(Admin admin);
}
