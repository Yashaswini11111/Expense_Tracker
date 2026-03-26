package com.finance.tracker;

import jakarta.persistence.*;

/**
 * Entity for Milestone 3 & 4: Budget Setting and Visual Tracking.
 * Stores the monthly limits set by the user for specific categories.
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category;

    @Column(name = "budget_amount", nullable = false)
    private Double budgetAmount;

    
    @Column(name = "month_year", length = 10)
    private String monthYear;

    
    public Budget() {}

    public Budget(Long userId, String category, Double budgetAmount, String monthYear) {
        this.userId = userId;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.monthYear = monthYear;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Double budgetAmount) { this.budgetAmount = budgetAmount; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
}