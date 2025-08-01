package com.example.adadminservice.infrastructure.email;

import com.example.adadminservice.application.out.mail.MailSender;
import com.example.adadminservice.infrastructure.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncMailSender implements MailSender {
    private final MessagePublisher<MailSendMessage> messagePublisher;

    @Override
    public void send(String to, String subject, String content) {
        messagePublisher.publish("mail", new MailSendMessage(to, subject, content));
    }
}
