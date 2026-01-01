package com.ms.data_collection_service.controller;

import com.ms.data_collection_service.entity.FinancialData;
import com.ms.data_collection_service.service.DataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataCollectionController {

    @Autowired
    private DataCollectionService dataCollectionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FinancialData>> getAllData(@PathVariable Long userId) {
        return ResponseEntity.ok(dataCollectionService.getAllDataForUser(userId));
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<FinancialData>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(dataCollectionService.getTransactionsForUser(userId));
    }

    @GetMapping("/{userId}/loans")
    public ResponseEntity<List<FinancialData>> getLoans(@PathVariable Long userId) {
        return ResponseEntity.ok(dataCollectionService.getLoansForUser(userId));
    }

    @PostMapping
    public ResponseEntity<FinancialData> submitData(@RequestBody FinancialData data) {
        return ResponseEntity.ok(dataCollectionService.submitData(data));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<FinancialData>> submitBatchData(@RequestBody List<FinancialData> dataList) {
        return ResponseEntity.ok(dataCollectionService.submitBatchData(dataList));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateData(@PathVariable Long userId, @RequestBody FinancialData data) {
        try {
            return ResponseEntity.ok(dataCollectionService.updateData(userId, data));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteData(@PathVariable Long userId) {
        dataCollectionService.deleteDataForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
