package com.example.adservice.infrastructure.mail;

import java.util.Map;

public record MailSendMessage(String to, String subject,
                              String content,
                              String templateName,
                              Map<String, Object> variables) {
    public static MailSendMessage withTemplate(
            String to, String subject,
            String templateName, Map<String, Object> variables
    ) {
        return new MailSendMessage(to, subject, null, templateName, variables);
    }

    // 일반 텍스트 생성자
    public static MailSendMessage plainText(
            String to, String subject, String textContent
    ) {
        return new MailSendMessage(to, subject, textContent, null, null);
    }
}
