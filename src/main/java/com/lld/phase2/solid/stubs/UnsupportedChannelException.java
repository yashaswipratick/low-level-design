package com.lld.phase2.solid.stubs;

public class UnsupportedChannelException extends RuntimeException {
    public UnsupportedChannelException(String channel) {
        super("Unsupported notification channel: " + channel);
    }
}
