package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    @Autowired 
    private TransactionRepository transactionRepo;

    @Autowired 
    private BudgetRepository budgetRepo;
    
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/budget-vs-spent/{userId}")
    public List<BudgetSummaryDTO> getComparison(@PathVariable Long userId) {
        List<BudgetSummaryDTO> spending = transactionRepo.findSpendingByCategoryForCurrentMonth(userId);
        
        List<Budget> userBudgets = budgetRepo.findByUserId(userId);

        for (BudgetSummaryDTO dto : spending) {
            userBudgets.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(dto.getCategory()))
                .findFirst()
                .ifPresent(b -> dto.setBudgetAmount(b.getBudgetAmount()));
        }
        return spending;
    }

    
    @GetMapping("/spending-trend/{userId}")
    public List<MonthlyTrendDTO> getTrend(@PathVariable Long userId) {
        List<MonthlyTrendDTO> data = transactionRepo.getMonthlySpendingTrend(userId);
        
        System.out.println(">>> Analytics: Trend records found for user " + userId + ": " + data.size());
        return data;
    }

    
    @GetMapping("/income-vs-expense/{userId}")
    public Map<String, Double> getTotals(@PathVariable Long userId) {
        List<Transaction> txns = transactionRepo.findByUserIdOrderByDateDesc(userId);
        
        double incomeTotal = txns.stream()
            .filter(t -> "Income".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
            
        double expenseTotal = txns.stream()
            .filter(t -> "Expense".equalsIgnoreCase(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();

        Double monthlySalary = userRepo.findById(userId)
                .map(User::getMonthlyIncome)
                .orElse(0.0);
        
        Map<String, Double> summary = new HashMap<>();
        summary.put("income", incomeTotal);
        summary.put("expense", expenseTotal);
        summary.put("salary", monthlySalary); 
        summary.put("balance", monthlySalary - expenseTotal);
        
        return summary;
    }
}