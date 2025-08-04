package com.example.adservice.domain.model;

import lombok.Getter;

import java.util.UUID;


@Getter
public class Advertiser {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String email;
    private AuditInfo auditInfo;


    public Advertiser(UUID id, String username, String password, String name, String email, AuditInfo auditInfo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.auditInfo = auditInfo;
    }

    public static Advertiser create(String username, String password, String name, String email) {
        return new Advertiser(null, username, password, name, email, AuditInfo.create(username));
    }
}
