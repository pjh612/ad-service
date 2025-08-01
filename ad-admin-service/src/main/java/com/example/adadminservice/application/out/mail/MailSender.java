package com.example.adadminservice.application.out.mail;

public interface MailSender {
    void send(String to, String subject, String content);
}
