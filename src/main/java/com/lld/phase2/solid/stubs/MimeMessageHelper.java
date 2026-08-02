package com.lld.phase2.solid.stubs;

/** Stub for org.springframework.mail.javamail.MimeMessageHelper. */
public class MimeMessageHelper {
    public MimeMessageHelper(MimeMessage msg, boolean multipart) throws MessagingException {}
    public void setTo(String to)                throws MessagingException {}
    public void setSubject(String subject)       throws MessagingException {}
    public void setText(String text, boolean html) throws MessagingException {}
}
