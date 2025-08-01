package com.example.adadminservice.infrastructure.web.controller;

import com.example.adadminservice.application.in.CreateAdZoneUseCase;
import com.example.adadminservice.application.in.QueryAdZoneUseCase;
import com.example.adadminservice.application.in.dto.AdZoneResponse;
import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;
import com.example.adadminservice.domain.model.AdZoneState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-zones")
public class AdZoneController {
    private final CreateAdZoneUseCase createAdZoneUseCase;
    private final QueryAdZoneUseCase queryAdZoneUseCase;

    @PostMapping
    public CreateAdZoneResponse createAdZone(@RequestBody CreateAdZoneRequest request) {
        return createAdZoneUseCase.create(request);
    }

    @GetMapping
    public Page<AdZoneResponse> getAdZones(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AdZoneState state,
            @PageableDefault(size = 20) Pageable pageable) {

        return queryAdZoneUseCase.getAdZones(name, state, pageable);
    }
}
