package com.finance.tracker;


public class MonthlyTrendDTO {

    private final String month;
    private final Double amount;

    
    public MonthlyTrendDTO(String month, Double amount) {
        this.month = (month != null) ? month : "Unknown";
        this.amount = (amount != null) ? amount : 0.0;
    }

    
    public String getMonth() {
        return month;
    }

    public Double getAmount() {
        return amount;
    }

  
    @Override
    public String toString() {
        return "MonthlyTrendDTO{" +
                "month='" + month + '\'' +
                ", totalAmount=" + amount +
                '}';
    }
}