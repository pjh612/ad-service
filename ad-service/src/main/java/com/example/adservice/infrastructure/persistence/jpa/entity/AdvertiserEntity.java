package com.example.adservice.infrastructure.persistence.jpa.entity;

import com.example.adservice.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "advertisers")
@Getter
@NoArgsConstructor
public class AdvertiserEntity extends BaseEntity {

    @Id
    @UuidV7Generator
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    public AdvertiserEntity(UUID id, String username, String password, String name, String email, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }
}
