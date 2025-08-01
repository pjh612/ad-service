package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.QueryAdZoneUseCase;
import com.example.adadminservice.application.in.dto.AdZoneResponse;
import com.example.adadminservice.domain.model.AdZoneState;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryAdZoneService implements QueryAdZoneUseCase {
    private final AdZoneRepository adZoneRepository;

    @Override
    public Page<AdZoneResponse> getAdZones(String name, AdZoneState state, Pageable pageable) {
        return adZoneRepository.find(name, state, pageable)
                .map(AdZoneResponse::from);
    }
}
