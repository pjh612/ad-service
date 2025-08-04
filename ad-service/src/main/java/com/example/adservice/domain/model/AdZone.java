package com.example.adservice.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AdZone {
    private UUID id;
    private String name;
    private String description;
    private int width;
    private int height;
    private AdZoneState adZoneState;
    private AuditInfo auditInfo;

    public AdZone(UUID id, String name, String description, int width, int height, AdZoneState adZoneState, AuditInfo auditInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.adZoneState = adZoneState;
        this.auditInfo = auditInfo;
    }
}
