package com.ms.credit_scoring_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialDataDto {
    private Long id;
    private Long userId;
    private String dataType;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
}
