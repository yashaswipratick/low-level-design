package com.lld.phase6.patterns.creational.factory.problem2.impl;

import com.lld.phase6.patterns.creational.factory.problem2.NotificationSender;

public class PushNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending push notification to " + recipient + ": " + message);
    }
}
