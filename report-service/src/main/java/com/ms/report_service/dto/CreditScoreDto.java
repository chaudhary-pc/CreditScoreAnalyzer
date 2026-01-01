package com.ms.report_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditScoreDto {
    private Long creditScoreId;
    private Long userId;
    private Integer score;
    private LocalDate date;
    private String scoreType;
    private String scoreHistory;
    private String algorithmUsed;
}
