package com.ms.data_collection_service.service;

import com.ms.data_collection_service.client.UserClient;
import com.ms.data_collection_service.dto.UserDto;
import com.ms.data_collection_service.entity.FinancialData;
import com.ms.data_collection_service.repository.FinancialDataRepository;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataCollectionService {

    private static final Logger logger = LogManager.getLogger(DataCollectionService.class);

    @Autowired
    private FinancialDataRepository financialDataRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<FinancialData> getAllDataForUser(Long userId) {
        logger.debug("Fetching all financial data for user ID: {}", userId);
        validateUserExists(userId);
        return financialDataRepository.findByUserId(userId);
    }

    public List<FinancialData> getTransactionsForUser(Long userId) {
        logger.debug("Fetching transaction data for user ID: {}", userId);
        validateUserExists(userId);
        return financialDataRepository.findByUserIdAndDataType(userId, "TRANSACTION");
    }

    public List<FinancialData> getLoansForUser(Long userId) {
        logger.debug("Fetching loan data for user ID: {}", userId);
        validateUserExists(userId);
        return financialDataRepository.findByUserIdAndDataType(userId, "LOAN");
    }

    public FinancialData submitData(FinancialData data) {
        logger.info("Submitting new financial data for user ID: {}", data.getUserId());
        validateUserExists(data.getUserId());
        FinancialData savedData = financialDataRepository.save(data);
        
        // Publish event to Kafka
        kafkaProducerService.sendDataUpdatedEvent(savedData.getUserId());
        
        return savedData;
    }

    public List<FinancialData> submitBatchData(List<FinancialData> dataList) {
        logger.info("Submitting batch of {} financial data records.", dataList.size());
        
        // Validate all users in the batch
        List<Long> userIds = dataList.stream()
                .map(FinancialData::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        userIds.forEach(this::validateUserExists);
        
        List<FinancialData> savedData = financialDataRepository.saveAll(dataList);
        
        // Publish event for each unique user ID
        userIds.forEach(kafkaProducerService::sendDataUpdatedEvent);
        
        return savedData;
    }

    public FinancialData updateData(Long userId, FinancialData newData) {
        logger.debug("Updating financial data for user ID: {}", userId);
        validateUserExists(userId);
        
        if (financialDataRepository.findByUserId(userId).isEmpty()) {
            logger.error("Update failed: No financial data found with ID: {}", userId);
            throw new RuntimeException("Data not found");
        }
        newData.setUserId(userId);
        FinancialData savedData = financialDataRepository.save(newData);
        
        // Publish event to Kafka
        kafkaProducerService.sendDataUpdatedEvent(savedData.getUserId());
        
        return savedData;
    }

    public void deleteDataForUser(Long userId) {
        logger.warn("Deleting all financial data for user ID: {}", userId);
        validateUserExists(userId);
        List<FinancialData> dataToDelete = financialDataRepository.findByUserId(userId);
        financialDataRepository.deleteAll(dataToDelete);
        logger.info("Successfully deleted {} records for user ID: {}", dataToDelete.size(), userId);
    }

    private void validateUserExists(Long userId) {
        try {
            UserDto user = userClient.getUserById(userId);
            if (user == null) {
                logger.error("User validation failed: User ID {} returned null.", userId);
                throw new RuntimeException("User not found");
            }
            logger.debug("User validation successful for ID: {}", userId);
        } catch (FeignException.NotFound e) {
            logger.error("User validation failed: User ID {} not found in User Service.", userId);
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            logger.error("User validation failed: Error communicating with User Service for ID {}. Error: {}", userId, e.getMessage());
            throw new RuntimeException("Error validating user: " + e.getMessage());
        }
    }
}
