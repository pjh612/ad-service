package com.example.mailconsumerservice.infrastructure;

import java.util.Map;

public interface TemplateEnginePort {
    String processTemplate(String templateName, Map<String, Object> variables);
}
