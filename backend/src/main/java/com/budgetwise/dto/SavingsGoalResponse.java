package com.budgetwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalResponse {
    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remaining;
    private LocalDate targetDate;
    private String description;
    private Boolean achieved;
    private Double percentageAchieved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
