package com.example.mailconsumerservice.domain;

public record MailSendMessage(
        String to,
        String subject,
        String content
) {
}
