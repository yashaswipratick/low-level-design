package com.lld.phase6.patterns.creational.factory.problem2;

import com.lld.phase6.patterns.creational.factory.problem2.factory.NotificationSenderFactory;

public class NotificationService {
    private final NotificationSenderFactory factory;


    public NotificationService(NotificationSenderFactory factory) {
        this.factory = factory;
    }

    public void sendNotification(NotificationChannel channel, String recipient , String message) {
        NotificationSender notificationSender = factory.create(channel);
        notificationSender.send(recipient, message);
        System.out.println("Notification sent successfully via " + channel + " to " + recipient);
    }
}
