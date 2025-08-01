package com.example.adadminservice.application.in;

import com.example.adadminservice.application.in.dto.CreateAdminRequest;
import com.example.adadminservice.application.in.dto.CreateAdminResponse;

public interface CreateAdminUseCase {
    CreateAdminResponse create(CreateAdminRequest request);
}
