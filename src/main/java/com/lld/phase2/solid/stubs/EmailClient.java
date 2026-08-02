package com.lld.phase2.solid.stubs;

/** Stub — generic email sending client (used in SRP, OCP problems). */
public interface EmailClient {
    void send(String to, String subject, String body);
}
