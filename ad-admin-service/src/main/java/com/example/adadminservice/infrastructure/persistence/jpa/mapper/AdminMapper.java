package com.example.adadminservice.infrastructure.persistence.jpa.mapper;

import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.infrastructure.persistence.jpa.entity.AdminEntity;

public class AdminMapper {

    public static AdminEntity toEntity(Admin domain) {
        return AdminEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .department(domain.getDepartment())
                .active(domain.isActive())
                .lastLoginAt(domain.getLastLoginAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static Admin toDomain(AdminEntity entity) {
        return new Admin(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDepartment(),
                entity.isActive(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
