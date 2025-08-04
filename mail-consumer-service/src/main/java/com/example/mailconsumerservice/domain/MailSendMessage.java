package com.example.mailconsumerservice.domain;

import java.util.Map;

public record MailSendMessage(String to, String subject,
                              String content,
                              String templateName,
                              Map<String, Object> variables) {
}
