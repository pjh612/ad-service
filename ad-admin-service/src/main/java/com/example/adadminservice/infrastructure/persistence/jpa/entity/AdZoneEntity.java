package com.example.adadminservice.infrastructure.persistence.jpa.entity;

import com.example.adadminservice.common.hibernate.annotations.UuidV7Generator;
import com.example.adadminservice.domain.model.AdZoneState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "ad_zones")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdZoneEntity {

    @Id
    @UuidV7Generator
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "width", nullable = false)
    private int width;

    @Column(name = "height", nullable = false)
    private int height;


    @Column(name = "ad_zone_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdZoneState adZoneState;

    public AdZoneEntity(UUID id, String name, String description, int width, int height, AdZoneState adZoneState) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.adZoneState = adZoneState;
    }
}
