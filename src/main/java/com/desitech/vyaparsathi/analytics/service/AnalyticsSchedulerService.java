package com.desitech.vyaparsathi.analytics.service;

import com.desitech.vyaparsathi.analytics.dto.ItemDemandPredictionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import java.util.List;

@Service
public class AnalyticsSchedulerService {
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private AnalyticsExportService analyticsExportService;
    @Autowired
    private JavaMailSender mailSender;


    // Daily report (CSV)
    @Scheduled(cron = "0 0 7 * * *") // Every day at 7 AM
    public void sendDailyItemDemandReport() {
        List<ItemDemandPredictionDto> data = analyticsService.predictItemDemand(null);
        byte[] csv = analyticsExportService.exportItemDemand(data, "csv");
        sendEmailWithAttachment("admin@example.com", "Daily Item Demand Report", "See attached report.", csv, "item-demand.csv");
    }

    // Weekly report (Excel)
    @Scheduled(cron = "0 0 8 * * MON") // Every Monday at 8 AM
    public void sendWeeklyItemDemandReport() {
        List<ItemDemandPredictionDto> data = analyticsService.predictItemDemand(null);
        byte[] excel = analyticsExportService.exportItemDemand(data, "excel");
        sendEmailWithAttachment("admin@example.com", "Weekly Item Demand Report", "See attached Excel report.", excel, "item-demand.xlsx");
    }

    // Monthly report (PDF)
    @Scheduled(cron = "0 0 9 1 * *") // 1st of every month at 9 AM
    public void sendMonthlyItemDemandReport() {
        List<ItemDemandPredictionDto> data = analyticsService.predictItemDemand(null);
        byte[] pdf = analyticsExportService.exportItemDemand(data, "pdf");
        sendEmailWithAttachment("admin@example.com", "Monthly Item Demand Report", "See attached PDF report.", pdf, "item-demand.pdf");
    }

    private void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(filename, new org.springframework.core.io.ByteArrayResource(attachment));
            mailSender.send(message);
        } catch (Exception e) {
            // Log error
            e.printStackTrace();
        }
    }
}
