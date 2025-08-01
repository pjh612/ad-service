package com.example.adadminservice.infrastructure.messaging;

public interface MessagePublisher<T> {
    void publish(String topic, T message);
}
