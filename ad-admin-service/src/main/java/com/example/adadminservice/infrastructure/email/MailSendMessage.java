package com.example.adadminservice.infrastructure.email;

public record MailSendMessage(String to, String subject, String content) {
}
