package com.lld.phase2.solid.stubs;

public class UnsupportedFormatException extends RuntimeException {
    public UnsupportedFormatException(String format) {
        super("Unsupported export format: " + format);
    }
}
