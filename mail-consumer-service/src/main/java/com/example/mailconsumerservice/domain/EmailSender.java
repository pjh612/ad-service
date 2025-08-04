package com.example.mailconsumerservice.domain;

import java.util.Map;

public interface EmailSender {
    void send(String to, String subject, String content);

    void send(String to, String subject, Map<String, Object> variables, String templateName);
}
