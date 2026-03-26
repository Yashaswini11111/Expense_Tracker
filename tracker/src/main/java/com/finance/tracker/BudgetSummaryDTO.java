package com.finance.tracker;


public class BudgetSummaryDTO {
    
    private String category;
    private Double budgetAmount;
    private Double actualSpent;

    
    public BudgetSummaryDTO(String category, Double actualSpent) {
        this.category = category;
        this.actualSpent = (actualSpent != null) ? actualSpent : 0.0;
        this.budgetAmount = 0.0; 
    }

   
    public BudgetSummaryDTO(String category, Double budgetAmount, Double actualSpent) {
        this.category = category;
        this.budgetAmount = (budgetAmount != null) ? budgetAmount : 0.0;
        this.actualSpent = (actualSpent != null) ? actualSpent : 0.0;
    }

    public Double getRemaining() {
        return this.budgetAmount - this.actualSpent;
    }

    
    public Double getPercentUsed() {
        if (this.budgetAmount == 0) return 0.0;
        return (this.actualSpent / this.budgetAmount) * 100;
    }


    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Double budgetAmount) { this.budgetAmount = budgetAmount; }

    public Double getActualSpent() { return actualSpent; }
    public void setActualSpent(Double actualSpent) { this.actualSpent = actualSpent; }

    @Override
    public String toString() {
        return "BudgetSummaryDTO{" +
                "category='" + category + '\'' +
                ", budget=" + budgetAmount +
                ", spent=" + actualSpent +
                ", remaining=" + getRemaining() +
                '}';
    }
}