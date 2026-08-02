package com.lld.phase2.solid.stubs;

/** Stub for org.springframework.mail.javamail.JavaMailSender. */
public interface JavaMailSender {
    MimeMessage createMimeMessage();
    void send(MimeMessage message);
}
