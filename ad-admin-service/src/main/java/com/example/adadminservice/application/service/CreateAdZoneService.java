package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.CreateAdZoneUseCase;
import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdZoneService implements CreateAdZoneUseCase {
    private final AdZoneRepository adZoneRepository;

    @Override
    @Transactional
    public CreateAdZoneResponse create(CreateAdZoneRequest request) {
        AdZone adZone = AdZone.create(request.title(), request.description(), request.width(), request.height());
        AdZone saved = adZoneRepository.save(adZone);

        return new CreateAdZoneResponse(saved.getId());
    }
}
