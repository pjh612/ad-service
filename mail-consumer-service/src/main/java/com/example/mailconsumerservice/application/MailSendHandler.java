package com.example.mailconsumerservice.application;

import com.example.mailconsumerservice.domain.EmailSender;
import com.example.mailconsumerservice.domain.MailSendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailSendHandler {
    private final EmailSender emailSender;

    @KafkaListener(topics = "mail", groupId = "mail-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload MailSendMessage payload) {
        log.info("Message received: {}", payload);

        emailSender.send(payload.to(), payload.subject(), payload.content());
    }
}
