package com.example.adadminservice.infrastructure.messaging;

import com.example.adadminservice.application.out.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher<T> implements MessagePublisher<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @Override
    public void publish(String topic, T message) {
        kafkaTemplate.send(topic, message);
    }
}
