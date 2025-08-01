package com.example.adadminservice.application.in.dto;

import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.AdZoneState;

import java.util.UUID;

public record AdZoneResponse(
        UUID id,
        String name,
        String description,
        int width,
        int height,
        AdZoneState state
) {
    public static AdZoneResponse from(AdZone adZone) {
        return new AdZoneResponse(
                adZone.getId(),
                adZone.getName(),
                adZone.getDescription(),
                adZone.getWidth(),
                adZone.getHeight(),
                adZone.getAdZoneState()
        );
    }
}