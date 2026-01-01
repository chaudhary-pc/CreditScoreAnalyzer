package com.ms.credit_scoring_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_score_id")
    private Long creditScoreId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "score_type", nullable = false)
    private String scoreType; // e.g., FICO, VantageScore

    @Column(name = "score_history", columnDefinition = "TEXT")
    private String scoreHistory; // Storing JSON as String

    @Column(name = "algorithm_used")
    private String algorithmUsed;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
