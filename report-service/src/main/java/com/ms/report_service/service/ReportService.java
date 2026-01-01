package com.ms.report_service.service;

import com.ms.report_service.client.CreditScoreClient;
import com.ms.report_service.client.UserClient;
import com.ms.report_service.dto.CreditScoreDto;
import com.ms.report_service.dto.UserDto;
import com.ms.report_service.entity.Report;
import com.ms.report_service.repository.ReportRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CreditScoreClient creditScoreClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private JavaMailSender javaMailSender;

    public Report generateReport(Long userId) {
        logger.info("Generating credit report for user ID: {}", userId);

        // 1. Fetch Credit Score
        CreditScoreDto creditScore = creditScoreClient.getScoreByUserId(userId);
        if (creditScore == null) {
            throw new RuntimeException("Credit score not found for user");
        }

        // 2. Compile Report Data (Simplified JSON construction)
        String reportData = buildReportData(creditScore);

        // 3. Save Report
        Report report = new Report();
        report.setUserId(userId);
        report.setCreditScoreId(creditScore.getCreditScoreId());
        report.setReportType("STANDARD");
        report.setReportData(reportData);

        Report savedReport = reportRepository.save(report);
        logger.info("Report generated successfully with ID: {}", savedReport.getReportId());
        return savedReport;
    }

    private String buildReportData(CreditScoreDto score) {
        // In a real app, use Jackson ObjectMapper to create JSON
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"score\": ").append(score.getScore()).append(",");
        sb.append("\"grade\": \"").append(calculateGrade(score.getScore())).append("\",");
        sb.append("\"recommendation\": \"").append(getRecommendation(score.getScore())).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String calculateGrade(int score) {
        if (score >= 800) return "Excellent";
        if (score >= 740) return "Very Good";
        if (score >= 670) return "Good";
        if (score >= 580) return "Fair";
        return "Poor";
    }

    private String getRecommendation(int score) {
        if (score >= 740) return "You are eligible for the best interest rates.";
        if (score >= 670) return "You have good credit, but could improve.";
        return "Consider paying off debts to improve your score.";
    }

    public List<Report> getReportsForUser(Long userId) {
        return reportRepository.findByUserId(userId);
    }

    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public Report updateReport(Long reportId, String newData) {
        Report report = getReportById(reportId);
        report.setReportData(newData);
        return reportRepository.save(report);
    }

    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    public void generateBatchReports(List<Long> userIds) {
        logger.info("Batch generating reports for {} users.", userIds.size());
        for (Long userId : userIds) {
            try {
                generateReport(userId);
            } catch (Exception e) {
                logger.error("Failed to generate report for user ID: {}", userId, e);
            }
        }
    }

    public Map<String, Object> getReportStats() {
        long totalReports = reportRepository.count();
        // Add more complex stats here if needed
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", totalReports);
        return stats;
    }

    public void sendReport(Long reportId, String recipientEmail) {
        logger.info("Sending report ID {} to {}", reportId, recipientEmail);
        Report report = getReportById(reportId);
        
        // If recipient is not provided, fetch user's email
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            UserDto user = userClient.getUserById(report.getUserId());
            recipientEmail = user.getEmail();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your Credit Report - ID: " + reportId);
        message.setText("Here is your credit report details:\n\n" + report.getReportData());

        try {
            javaMailSender.send(message);
            logger.info("Email sent successfully.");
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }
}
