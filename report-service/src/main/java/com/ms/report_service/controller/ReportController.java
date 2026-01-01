package com.ms.report_service.controller;

import com.ms.report_service.entity.Report;
import com.ms.report_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Report>> getUserReports(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsForUser(userId));
    }

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestParam Long userId) {
        return ResponseEntity.ok(reportService.generateReport(userId));
    }

    @GetMapping("/detail/{reportId}")
    public ResponseEntity<Report> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Report> updateReport(@PathVariable Long reportId, @RequestBody String newData) {
        return ResponseEntity.ok(reportService.updateReport(reportId, newData));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchReports(@RequestBody List<Long> userIds) {
        reportService.generateBatchReports(userIds);
        return ResponseEntity.ok("Batch generation started");
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(reportService.getReportStats());
    }

    @PostMapping("/{reportId}/send")
    public ResponseEntity<String> sendReport(@PathVariable Long reportId, @RequestParam(required = false) String email) {
        reportService.sendReport(reportId, email);
        return ResponseEntity.ok("Report sent successfully");
    }
}
