package com.example.adadminservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private UUID id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String department;
    private boolean active;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Admin create(String email, String password, String firstName, String lastName, String department) {
        return new Admin(
                null,
                email,
                password,
                firstName,
                lastName,
                department,
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
