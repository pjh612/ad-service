package com.example.adservice.infrastructure.mail;

import com.example.adservice.application.out.mail.MailSender;
import com.example.adservice.application.out.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncMailSender implements MailSender {
    private final MessagePublisher<MailSendMessage> messagePublisher;

    @Override
    public void send(String to, String subject, String content) {
        log.info("Sending mail to {}", to);
        messagePublisher.publish("mail", MailSendMessage.plainText(to, subject, content));
    }

    @Override
    public void send(String to, String subject, Map<String, Object> context, String templateName) {
        log.info("Sending mail to {}", to);
        messagePublisher.publish("template-mail", MailSendMessage.withTemplate(to, subject, templateName, context));
    }
}
