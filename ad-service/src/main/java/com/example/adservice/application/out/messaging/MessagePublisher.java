package com.example.adservice.application.out.messaging;

public interface MessagePublisher<T> {
    void publish(String topic, T message);
}
