package com.ms.credit_scoring_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LogManager.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "score-calculated";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendScoreCalculatedEvent(Long userId) {
        logger.info("Publishing score-calculated event for user ID: {}", userId);
        kafkaTemplate.send(TOPIC, String.valueOf(userId));
    }
}
