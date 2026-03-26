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
public class TransactionResponse {
    private Long id;
    private String name;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate date;
    private String description;
    private LocalDateTime createdAt;
}
