package com.ms.credit_scoring_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerService.class);
    private static final String TOPIC = "data-updated";
    private static final String GROUP_ID = "credit-scoring-group";

    @Autowired
    private CreditScoreService creditScoreService;

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void listenDataUpdated(String message) {
        logger.info("Received Kafka message: {}", message);
        try {
            Long userId = Long.valueOf(message);
            logger.info("Triggering score recalculation for user ID: {}", userId);
            
            // Recalculate the score
            creditScoreService.calculateScore(userId);
            
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID in Kafka message: {}", message);
        } catch (Exception e) {
            logger.error("Error processing Kafka message for user ID: {}", message, e);
        }
    }
}
