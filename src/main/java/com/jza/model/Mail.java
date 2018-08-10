package com.jza.model;

import java.util.HashMap;
import java.util.Map;

public class Mail {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> model = new HashMap<>();

    @Override
    public String toString() {
        return "mail{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", template='" + template + '\'' +
                ", model=" + model +
                '}';
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
