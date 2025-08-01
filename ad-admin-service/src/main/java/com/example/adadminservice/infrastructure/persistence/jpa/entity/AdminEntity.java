package com.example.adadminservice.infrastructure.persistence.jpa.entity;

import com.example.adadminservice.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminEntity {

    @Id
    @UuidV7Generator
    private UUID id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String department;

    private boolean active;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

