package com.example.adadminservice.application.in;

import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;

public interface CreateAdZoneUseCase {
    CreateAdZoneResponse create(CreateAdZoneRequest request);
}
