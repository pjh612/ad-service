package com.example.adadminservice.application.in.dto;

public record CreateAdminRequest(
        String email,
        String firstName,
        String lastName,
        String department
) {}
