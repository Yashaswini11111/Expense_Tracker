package com.budgetwise.service;

import com.budgetwise.dto.BudgetRequest;
import com.budgetwise.dto.BudgetResponse;
import com.budgetwise.model.Budget;
import com.budgetwise.model.Transaction.TransactionType;
import com.budgetwise.model.User;
import com.budgetwise.repository.BudgetRepository;
import com.budgetwise.repository.TransactionRepository;
import com.budgetwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        User user = getCurrentUser();
        
        // Check if budget already exists for this category and period
        Optional<Budget> existing = budgetRepository.findByUserAndCategoryAndMonthAndYear(
                user, request.getCategory(), request.getMonth(), request.getYear());
        
        if (existing.isPresent()) {
            throw new RuntimeException("Budget already exists for this category and period");
        }
        
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setPeriod(request.getPeriod());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        
        // Calculate current spent amount
        BigDecimal spent = calculateSpent(user, request.getCategory(), request.getMonth(), request.getYear());
        budget.setSpent(spent);
        
        Budget saved = budgetRepository.save(budget);
        return mapToResponse(saved);
    }
    
    public List<BudgetResponse> getAllBudgets() {
        User user = getCurrentUser();
        return budgetRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<BudgetResponse> getBudgetsByMonthAndYear(Integer month, Integer year) {
        User user = getCurrentUser();
        return budgetRepository.findByUserAndMonthAndYear(user, month, year).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public BudgetResponse getBudgetById(Long id) {
        User user = getCurrentUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToResponse(budget);
    }
    
    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        User user = getCurrentUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setPeriod(request.getPeriod());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        
        // Recalculate spent amount
        BigDecimal spent = calculateSpent(user, request.getCategory(), request.getMonth(), request.getYear());
        budget.setSpent(spent);
        
        Budget updated = budgetRepository.save(budget);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteBudget(Long id) {
        User user = getCurrentUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        budgetRepository.delete(budget);
    }
    
    @Transactional
    public void updateBudgetSpent(User user, String category, Integer month, Integer year) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserAndCategoryAndMonthAndYear(
                user, category, month, year);
        
        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            BigDecimal spent = calculateSpent(user, category, month, year);
            budget.setSpent(spent);
            budgetRepository.save(budget);
        }
    }
    
    private BigDecimal calculateSpent(User user, String category, Integer month, Integer year) {
        BigDecimal spent = transactionRepository.sumAmountByUserAndTypeAndCategoryAndMonthAndYear(
                user, TransactionType.EXPENSE, category, month, year);
        return spent != null ? spent : BigDecimal.ZERO;
    }
    
    private BudgetResponse mapToResponse(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setCategory(budget.getCategory());
        response.setAmount(budget.getAmount());
        response.setSpent(budget.getSpent());
        response.setRemaining(budget.getAmount().subtract(budget.getSpent()));
        response.setPeriod(budget.getPeriod());
        response.setMonth(budget.getMonth());
        response.setYear(budget.getYear());
        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());
        
        // Calculate percentage used
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            double percentage = budget.getSpent()
                    .divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            response.setPercentageUsed(percentage);
        } else {
            response.setPercentageUsed(0.0);
        }
        
        return response;
    }
}
