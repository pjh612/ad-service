package com.example.adadminservice.infrastructure.web.controller;

import com.example.adadminservice.application.in.dto.CreateAdminRequest;
import com.example.adadminservice.application.in.dto.CreateAdminResponse;
import com.example.adadminservice.application.in.CreateAdminUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class CreateAdminController {
    private final CreateAdminUseCase createAdminUseCase;

    @PostMapping
    public CreateAdminResponse createAdmin(@RequestBody CreateAdminRequest request) {
        return createAdminUseCase.create(request);
    }

}
