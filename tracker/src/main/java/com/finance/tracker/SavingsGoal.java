package com.finance.tracker;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goal_name", nullable = false)
    private String goalName;

    @Column(name = "target_amount", nullable = false)
    private Double targetAmount;

    @Column(name = "current_amount")
    private Double currentAmount = 0.0;

    @Column(nullable = false)
    private LocalDate deadline;


    public SavingsGoal() {}

    public SavingsGoal(Long userId, String goalName, Double targetAmount, LocalDate deadline) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.deadline = deadline;
    }


     
    @Transient 
    public Double getProgressPercentage() {
        if (targetAmount == null || targetAmount <= 0) return 0.0;
        double percent = (currentAmount / targetAmount) * 100;
        return Math.min(percent, 100.0); 
    }

    
    @Transient
    public Double getAmountRemaining() {
        if (targetAmount == null) return 0.0;
        return Math.max(targetAmount - currentAmount, 0.0);
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(Double currentAmount) { this.currentAmount = currentAmount; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}