package com.example.mailconsumerservice.domain;

public interface EmailSender {
    void send(String to, String subject, String content);
}
