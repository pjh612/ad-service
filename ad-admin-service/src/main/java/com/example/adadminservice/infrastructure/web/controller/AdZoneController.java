package com.example.adadminservice.infrastructure.web.controller;

import com.example.adadminservice.application.in.CreateAdZoneUseCase;
import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-zones")
public class AdZoneController {
    private final CreateAdZoneUseCase createAdZoneUseCase;

    @PostMapping
    public CreateAdZoneResponse createAdZone(@RequestBody CreateAdZoneRequest request) {
        return createAdZoneUseCase.create(request);
    }
}
