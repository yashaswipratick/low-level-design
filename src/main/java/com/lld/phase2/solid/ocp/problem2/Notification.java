package com.lld.phase2.solid.ocp.problem2;

/** Notification request — do not change this class. */
public class Notification {

    private final String channel;
    private final String message;
    private final String recipient;    // email address
    private final String phone;        // phone number for SMS
    private final String deviceToken;  // device token for PUSH

    public Notification(String channel, String message,
                        String recipient, String phone, String deviceToken) {
        this.channel     = channel;
        this.message     = message;
        this.recipient   = recipient;
        this.phone       = phone;
        this.deviceToken = deviceToken;
    }

    public String getChannel()     { return channel; }
    public String getMessage()     { return message; }
    public String getRecipient()   { return recipient; }
    public String getPhone()       { return phone; }
    public String getDeviceToken() { return deviceToken; }
}
