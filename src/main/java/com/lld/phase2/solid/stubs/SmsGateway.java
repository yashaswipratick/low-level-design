package com.lld.phase2.solid.stubs;

/** Stub — SMS gateway client (used in OCP, DIP problems). */
public interface SmsGateway {
    void send(String phoneNumber, String message);
}
