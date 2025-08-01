package com.example.adadminservice.application.in;

import com.example.adadminservice.application.in.dto.AdZoneResponse;
import com.example.adadminservice.domain.model.AdZoneState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAdZoneUseCase {
    Page<AdZoneResponse> getAdZones(String name, AdZoneState state, Pageable pageable);
}
