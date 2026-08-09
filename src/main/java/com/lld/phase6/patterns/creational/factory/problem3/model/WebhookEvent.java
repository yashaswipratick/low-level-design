package com.lld.phase6.patterns.creational.factory.problem3.model;

public class WebhookEvent {
    private String eventType;
    private String data;

    public WebhookEvent(String eventType, String data) {
        this.eventType = eventType;
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "WebhookEvent{" +
                "eventType='" + eventType + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
