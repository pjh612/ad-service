package com.example.adadminservice.infrastructure.persistence;

import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.domain.repository.AdminRepository;
import com.example.adadminservice.infrastructure.persistence.jpa.mapper.AdminMapper;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryAdapter implements AdminRepository {

    private final AdminJpaRepository adminJpaRepository;

    @Override
    public Optional<Admin> findById(UUID id) {
        return adminJpaRepository.findById(id)
                .map(AdminMapper::toDomain);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminJpaRepository.findByEmail(email)
                .map(AdminMapper::toDomain);
    }

    @Override
    public Admin save(Admin admin) {
        return AdminMapper.toDomain(
                adminJpaRepository.save(AdminMapper.toEntity(admin))
        );
    }
}
