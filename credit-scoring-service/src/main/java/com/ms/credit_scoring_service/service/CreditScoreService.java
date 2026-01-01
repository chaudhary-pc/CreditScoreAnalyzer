package com.ms.credit_scoring_service.service;

import com.ms.credit_scoring_service.client.DataCollectionClient;
import com.ms.credit_scoring_service.dto.FinancialDataDto;
import com.ms.credit_scoring_service.entity.CreditScore;
import com.ms.credit_scoring_service.repository.CreditScoreRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.CachePut;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreditScoreService {

    private static final Logger logger = LogManager.getLogger(CreditScoreService.class);

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    @Autowired
    private DataCollectionClient dataCollectionClient;

    // @Cacheable(value = "creditScores", key = "#userId")
    public CreditScore getScoreByUserId(Long userId) {
        logger.debug("Fetching credit score for user ID: {}", userId);
        return creditScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit score not found for user"));
    }

    // @CacheEvict(value = "creditScores", key = "#userId")
    public CreditScore calculateScore(Long userId) {
        logger.info("Calculating credit score for user ID: {}", userId);
        
        // 1. Fetch financial data
        List<FinancialDataDto> financialData = dataCollectionClient.getAllDataForUser(userId);
        
        // 2. Apply Scoring Algorithm (Simplified)
        int score = calculateSimpleScore(financialData);
        
        // 3. Save or Update Score
        CreditScore creditScore = creditScoreRepository.findByUserId(userId)
                .orElse(new CreditScore());
        
        creditScore.setUserId(userId);
        creditScore.setScore(score);
        creditScore.setDate(LocalDate.now());
        creditScore.setScoreType("FICO-SIMULATED");
        creditScore.setAlgorithmUsed("SimpleBalanceAlgorithm");
        
        // Update history (Simplified - just appending current score)
        String currentHistory = creditScore.getScoreHistory();
        String newEntry = LocalDate.now() + ":" + score;
        if (currentHistory == null || currentHistory.isEmpty()) {
            creditScore.setScoreHistory(newEntry);
        } else {
            creditScore.setScoreHistory(currentHistory + "," + newEntry);
        }

        return creditScoreRepository.save(creditScore);
    }

    private int calculateSimpleScore(List<FinancialDataDto> data) {
        // Dummy Algorithm:
        
        int baseScore = 300;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (FinancialDataDto item : data) {
            if (item.getAmount() != null) {
                totalAmount = totalAmount.add(item.getAmount());
            }
        }

        int adjustment = totalAmount.divide(new BigDecimal(100), BigDecimal.ROUND_DOWN).intValue();
        int finalScore = baseScore + adjustment;

        // Cap score between 300 and 850
        if (finalScore > 850) return 850;
        if (finalScore < 300) return 300;
        return finalScore;
    }

    // @CacheEvict(value = "creditScores", key = "#userId")
    public CreditScore updateScore(Long userId, Integer newScore) {
        logger.info("Manually updating score for user ID: {}", userId);
        CreditScore creditScore = creditScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit score not found"));
        creditScore.setScore(newScore);
        creditScore.setDate(LocalDate.now());
        return creditScoreRepository.save(creditScore);
    }

    // @CacheEvict(value = "creditScores", key = "#userId")
    public void deleteScore(Long userId) {
        logger.warn("Deleting credit score for user ID: {}", userId);
        CreditScore creditScore = creditScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit score not found"));
        creditScoreRepository.delete(creditScore);
    }

    public String getScoreHistory(Long userId) {
        // We can reuse the cached getScoreByUserId method here
        return getScoreByUserId(userId).getScoreHistory();
    }

    public void calculateBatchScores(List<Long> userIds) {
        logger.info("Batch calculating scores for {} users.", userIds.size());
        for (Long userId : userIds) {
            try {
                // This calls calculateScore, which handles CacheEvict automatically
                calculateScore(userId);
            } catch (Exception e) {
                logger.error("Failed to calculate score for user ID: {}", userId, e);
            }
        }
    }

    public Double getAverageScore() {
        return creditScoreRepository.findAverageScore();
    }
}
