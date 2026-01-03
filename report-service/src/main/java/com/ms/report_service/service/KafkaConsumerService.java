package com.ms.report_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerService.class);
    private static final String TOPIC = "score-calculated";
    private static final String GROUP_ID = "report-service-group";

    @Autowired
    private ReportService reportService;

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void listenScoreCalculated(String message) {
        logger.info("Received Kafka message: {}", message);
        try {
            Long userId = Long.valueOf(message);
            logger.info("Triggering report generation for user ID: {}", userId);
            
            // Generate the report
            reportService.generateReport(userId);
            
            // Optionally, send the email immediately
            // reportService.sendReport(report.getReportId(), null); 
            
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID in Kafka message: {}", message);
        } catch (Exception e) {
            logger.error("Error processing Kafka message for user ID: {}", message, e);
        }
    }
}
