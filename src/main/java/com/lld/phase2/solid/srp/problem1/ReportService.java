package com.lld.phase2.solid.srp.problem1;

import com.lld.phase2.solid.stubs.JavaMailSender;
import com.lld.phase2.solid.stubs.MimeMessage;
import com.lld.phase2.solid.stubs.MimeMessageHelper;
import com.lld.phase2.solid.stubs.MessagingException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: SRP VIOLATION — this class has 3 distinct reasons to change:
//   1. DB query / schema changes        → affects fetchSalesData()
//   2. HTML layout / format changes     → affects formatAsHtml()
//   3. Email provider / template changes → affects emailReport()
//
// Your task: split into SalesDataRepository, ReportFormatter, ReportEmailSender
// and make this class a thin orchestrator with zero business logic.

@Service
public class ReportService {

    private final DataSource      dataSource;
    private final JavaMailSender  mailSender;

    public ReportService(DataSource dataSource, JavaMailSender mailSender) {
        this.dataSource  = dataSource;
        this.mailSender  = mailSender;
    }

    // Concern 1: Data access — changes when DB schema changes
    public List<Map<String, Object>> fetchSalesData(LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM orders WHERE created_at BETWEEN ? AND ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch sales data", e);
        }
        return result;
    }

    // Concern 2: Formatting — changes when HTML layout changes
    public String formatAsHtml(List<Map<String, Object>> data) {
        StringBuilder html = new StringBuilder("<table border='1'>");
        html.append("<tr><th>Column</th><th>Value</th></tr>");
        for (Map<String, Object> row : data) {
            html.append("<tr>");
            row.forEach((k, v) -> html.append("<td>").append(k).append("</td>")
                                      .append("<td>").append(v).append("</td>"));
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    // Concern 3: Email delivery — changes when email provider or template changes
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
