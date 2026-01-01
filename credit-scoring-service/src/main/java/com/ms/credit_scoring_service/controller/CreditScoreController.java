package com.ms.credit_scoring_service.controller;

import com.ms.credit_scoring_service.entity.CreditScore;
import com.ms.credit_scoring_service.service.CreditScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/score")
public class CreditScoreController {

    @Autowired
    private CreditScoreService creditScoreService;

    @GetMapping("/{userId}")
    public ResponseEntity<CreditScore> getScore(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(creditScoreService.getScoreByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<CreditScore> calculateScore(@RequestParam Long userId) {
        return ResponseEntity.ok(creditScoreService.calculateScore(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CreditScore> updateScore(@PathVariable Long userId, @RequestParam Integer newScore) {
        try {
            return ResponseEntity.ok(creditScoreService.updateScore(userId, newScore));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long userId) {
        try {
            creditScoreService.deleteScore(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<String> getScoreHistory(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(creditScoreService.getScoreHistory(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<String> calculateBatchScores(@RequestBody List<Long> userIds) {
        creditScoreService.calculateBatchScores(userIds);
        return ResponseEntity.ok("Batch calculation started");
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageScore() {
        return ResponseEntity.ok(creditScoreService.getAverageScore());
    }

    @PutMapping("/refresh")
    public ResponseEntity<CreditScore> refreshScore(@RequestParam Long userId) {
        // Refresh is essentially re-calculating based on latest data
        return ResponseEntity.ok(creditScoreService.calculateScore(userId));
    }
}
