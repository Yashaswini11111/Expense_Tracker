package com.budgetwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private String category;
    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private String period;
    private Integer month;
    private Integer year;
    private Double percentageUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
