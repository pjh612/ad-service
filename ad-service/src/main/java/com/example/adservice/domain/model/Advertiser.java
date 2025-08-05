package com.example.adservice.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
public class Advertiser {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String businessNumber;
    private LocalDate startAt;
    private AuditInfo auditInfo;


    public Advertiser(UUID id, String username, String password, String name, String email, String businessNumber, LocalDate startAt, AuditInfo auditInfo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.businessNumber = businessNumber;
        this.startAt = startAt;
        this.auditInfo = auditInfo;
    }

    public static Advertiser create(String username, String password, String name, String email, String businessNumber, LocalDate startAt) {
        return new Advertiser(null, username, password, name, email, businessNumber, startAt, AuditInfo.create(username));
    }
}
