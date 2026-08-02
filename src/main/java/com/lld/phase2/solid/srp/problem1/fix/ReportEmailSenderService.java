package com.lld.phase2.solid.srp.problem1.fix;

import com.lld.phase2.solid.stubs.JavaMailSender;
import com.lld.phase2.solid.stubs.MessagingException;
import com.lld.phase2.solid.stubs.MimeMessage;
import com.lld.phase2.solid.stubs.MimeMessageHelper;
import org.springframework.stereotype.Component;

// TODO:
//   3. Email provider / template changes → fixed

@Component
public class ReportEmailSenderService {

    private final JavaMailSender mailSender;

    public ReportEmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void emailReport(String toAddress, String htmlContent) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(toAddress);
            helper.setSubject("Weekly Sales Report");
            helper.setText(htmlContent, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send report email", e);
        }
    }
}
