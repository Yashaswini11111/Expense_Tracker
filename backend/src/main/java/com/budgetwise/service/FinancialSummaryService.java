package com.budgetwise.service;

import com.budgetwise.dto.FinancialSummaryResponse;
import com.budgetwise.model.Transaction.TransactionType;
import com.budgetwise.model.User;
import com.budgetwise.repository.TransactionRepository;
import com.budgetwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialSummaryService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public FinancialSummaryResponse getFinancialSummary() {
        User user = getCurrentUser();
        
        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndType(user, TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserAndType(user, TransactionType.EXPENSE);
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        
        BigDecimal balance = totalIncome.subtract(totalExpenses);
        
        // Calculate savings rate
        BigDecimal savingsRate = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = balance.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        // Get expenses by category
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        List<Object[]> expenseResults = transactionRepository.sumAmountByUserAndTypeGroupByCategory(
                user, TransactionType.EXPENSE);
        for (Object[] result : expenseResults) {
            expensesByCategory.put((String) result[0], (BigDecimal) result[1]);
        }
        
        // Get income by category
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        List<Object[]> incomeResults = transactionRepository.sumAmountByUserAndTypeGroupByCategory(
                user, TransactionType.INCOME);
        for (Object[] result : incomeResults) {
            incomeByCategory.put((String) result[0], (BigDecimal) result[1]);
        }
        
        FinancialSummaryResponse response = new FinancialSummaryResponse();
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setBalance(balance);
        response.setSavingsRate(savingsRate);
        response.setExpensesByCategory(expensesByCategory);
        response.setIncomeByCategory(incomeByCategory);
        
        return response;
    }
}
