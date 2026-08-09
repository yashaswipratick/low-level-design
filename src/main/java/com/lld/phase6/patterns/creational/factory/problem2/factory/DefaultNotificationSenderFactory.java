package com.lld.phase6.patterns.creational.factory.problem2.factory;

import com.lld.phase6.patterns.creational.factory.problem2.NotificationChannel;
import com.lld.phase6.patterns.creational.factory.problem2.NotificationSender;
import com.lld.phase6.patterns.creational.factory.problem2.impl.EmailNotificationSender;
import com.lld.phase6.patterns.creational.factory.problem2.impl.PushNotificationSender;
import com.lld.phase6.patterns.creational.factory.problem2.impl.SmsNotificationSender;

public class DefaultNotificationSenderFactory implements NotificationSenderFactory {

    @Override
    public NotificationSender create(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> new EmailNotificationSender();
            case SMS   -> new SmsNotificationSender();
            case PUSH  -> new PushNotificationSender();
        };
    }
}
