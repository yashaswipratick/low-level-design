package com.lld.phase2.solid.ocp.problem2;

import com.lld.phase2.solid.stubs.EmailClient;
import com.lld.phase2.solid.stubs.SmsGateway;
import com.lld.phase2.solid.stubs.PushService;
import org.springframework.stereotype.Service;

// TODO: OCP VIOLATION — adding a new notification channel requires modifying dispatch().
//
// Existing channels: EMAIL, SMS, PUSH
// Requested: SLACK, WHATSAPP, IN_APP
//
// Every new channel:
//   1. Requires modifying this if/else chain
//   2. Risks breaking existing channel logic
//   3. Makes this method grow unboundedly
//
// Your task: refactor using Strategy + constructor registration.
//   1. Define NotificationSender interface: String channel() + void send(Notification)
//   2. Each channel = one @Component implementing NotificationSender
//   3. Dispatcher: inject List<NotificationSender>, build Map<String, NotificationSender>
//   4. dispatch() becomes a single map.get().send() — never changes for new channels

@Service
public class NotificationDispatcher {

    private final EmailClient  emailClient;
    private final SmsGateway   smsGateway;
    private final PushService  pushService;

    public NotificationDispatcher(EmailClient emailClient,
                                  SmsGateway smsGateway,
                                  PushService pushService) {
        this.emailClient = emailClient;
        this.smsGateway  = smsGateway;
        this.pushService = pushService;
    }

    public void dispatch(Notification notification) {
        if (notification.getChannel().equals("EMAIL")) {
            emailClient.send(notification.getRecipient(),
                             "Notification",
                             "[EMAIL] " + notification.getMessage());

        } else if (notification.getChannel().equals("SMS")) {
            // SMS has 160-character limit
            String body = notification.getMessage()
                .substring(0, Math.min(160, notification.getMessage().length()));
            smsGateway.send(notification.getPhone(), body);

        } else if (notification.getChannel().equals("PUSH")) {
            pushService.notify(notification.getDeviceToken(), notification.getMessage());

        }
        // Adding SLACK?     Modify here. OCP violated.
        // Adding WHATSAPP?  Modify here. OCP violated.
    }
}
