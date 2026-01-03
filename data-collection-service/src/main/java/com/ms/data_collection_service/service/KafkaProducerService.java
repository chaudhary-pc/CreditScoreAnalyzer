package com.ms.data_collection_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LogManager.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "data-updated";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendDataUpdatedEvent(Long userId) {
        logger.info("Publishing data-updated event for user ID: {}", userId);
        kafkaTemplate.send(TOPIC, String.valueOf(userId));
    }
}
