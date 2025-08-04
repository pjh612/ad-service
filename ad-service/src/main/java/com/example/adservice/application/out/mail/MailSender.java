package com.example.adservice.application.out.mail;

import java.util.Map;

public interface MailSender {
    void send(String to, String subject, String content);

    void send(String to, String subject, Map<String, Object> context, String templateName);
}
