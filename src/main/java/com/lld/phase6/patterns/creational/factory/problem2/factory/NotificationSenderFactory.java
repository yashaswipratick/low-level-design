package com.lld.phase6.patterns.creational.factory.problem2.factory;

import com.lld.phase6.patterns.creational.factory.problem2.NotificationChannel;
import com.lld.phase6.patterns.creational.factory.problem2.NotificationSender;

public interface NotificationSenderFactory {
    NotificationSender create(NotificationChannel channel);
}
