package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    
    @PostMapping("/set")
    public ResponseEntity<?> setBudget(@RequestBody Budget budget) {
        if (budget.getUserId() == null || budget.getCategory() == null || budget.getBudgetAmount() == null) {
            return ResponseEntity.badRequest().body("Error: Missing required budget data.");
        }

        if (budget.getMonthYear() == null || budget.getMonthYear().isEmpty()) {
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            budget.setMonthYear(currentMonth);
        }

        List<Budget> userBudgets = budgetRepository.findByUserId(budget.getUserId());
        
        Optional<Budget> existing = userBudgets.stream()
            .filter(b -> b.getCategory().equalsIgnoreCase(budget.getCategory()) 
                      && b.getMonthYear().equals(budget.getMonthYear()))
            .findFirst();

        if (existing.isPresent()) {
            Budget existingBudget = existing.get();
            existingBudget.setBudgetAmount(budget.getBudgetAmount());
            Budget updated = budgetRepository.save(existingBudget);
            return ResponseEntity.ok(updated);
        } else {
            Budget saved = budgetRepository.save(budget);
            return ResponseEntity.ok(saved);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getUserBudgets(@PathVariable Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return ResponseEntity.ok(budgets);
    }
}