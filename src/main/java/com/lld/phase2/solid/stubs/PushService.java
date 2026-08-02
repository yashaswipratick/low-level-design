package com.lld.phase2.solid.stubs;

/** Stub — push notification service (used in OCP problem 2). */
public interface PushService {
    void notify(String deviceToken, String message);
}
