package com.example.mailconsumerservice.infrastructure;

import com.example.mailconsumerservice.domain.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    private final TemplateEnginePort templateEngine;

    private static final String ENCODING = "UTF-8";

    @Override
    public void send(String to, String subject, String content) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = createHelper(mimeMessage, to, subject);
            helper.setText(content, true);
        };
        executeSend(preparator);
    }

    @Override
    public void send(String to, String subject, Map<String, Object> variables, String templateName) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = createHelper(mimeMessage, to, subject);
            String processedContent = templateEngine.processTemplate(templateName, variables);
            helper.setText(processedContent, true);
        };
        executeSend(preparator);
    }

    private MimeMessageHelper createHelper(MimeMessage mimeMessage, String to, String subject)
            throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ENCODING);
        helper.setTo(to);
        helper.setSubject(subject);
        return helper;
    }

    private void executeSend(MimeMessagePreparator preparator) {
        try {
            mailSender.send(preparator);
        } catch (MailException ex) {
            throw new RuntimeException("이메일 전송 실패: " + ex.getMessage(), ex);
        }
    }
}
