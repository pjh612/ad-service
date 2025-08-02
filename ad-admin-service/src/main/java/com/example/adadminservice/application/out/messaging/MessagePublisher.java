package com.example.adadminservice.application.out.messaging;

public interface MessagePublisher<T> {
    void publish(String topic, T message);
}
