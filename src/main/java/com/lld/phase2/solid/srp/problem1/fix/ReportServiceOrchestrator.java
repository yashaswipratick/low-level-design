package com.lld.phase2.solid.srp.problem1.fix;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceOrchestrator {

    private final ReportEmailSenderService emailSenderService;
    private final ReportFormatterService formatterService;
    private final SalesDataRepository salesDataRepository;

    public ReportServiceOrchestrator(ReportEmailSenderService emailSenderService, ReportFormatterService formatterService, SalesDataRepository salesDataRepository) {
        this.emailSenderService = emailSenderService;
        this.formatterService = formatterService;
        this.salesDataRepository = salesDataRepository;
    }

    public void generateAndSend(String emailTo, LocalDate from, LocalDate to) {
        List<Map<String, Object>> salesData = salesDataRepository.fetchSalesData(from, to);
        String htmlReport = formatterService.formatAsHtml(salesData);
        emailSenderService.emailReport(emailTo, htmlReport);
    }
 }
