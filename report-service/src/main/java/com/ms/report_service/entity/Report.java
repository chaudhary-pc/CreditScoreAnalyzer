package com.ms.report_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "credit_score_id", nullable = false)
    private Long creditScoreId;

    @CreationTimestamp
    @Column(name = "generated_date", updatable = false)
    private LocalDateTime generatedDate;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData; // Storing JSON details as String

    @Column(name = "report_type", nullable = false)
    private String reportType; // e.g., "STANDARD", "DETAILED"
}
