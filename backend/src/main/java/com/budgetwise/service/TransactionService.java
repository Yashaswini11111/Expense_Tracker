package com.budgetwise.service;

import com.budgetwise.dto.TransactionRequest;
import com.budgetwise.dto.TransactionResponse;
import com.budgetwise.model.Transaction;
import com.budgetwise.model.Transaction.TransactionType;
import com.budgetwise.model.User;
import com.budgetwise.repository.TransactionRepository;
import com.budgetwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetService budgetService;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = getCurrentUser();
        
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setName(request.getName());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.valueOf(request.getType().toUpperCase()));
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        
        Transaction saved = transactionRepository.save(transaction);
        
        // Update budget spent if it's an expense
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(user, request.getCategory(), 
                    request.getDate().getMonthValue(), request.getDate().getYear());
        }
        
        return mapToResponse(saved);
    }
    
    public List<TransactionResponse> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findByUserOrderByDateDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        return transactionRepository.findByUserAndDateBetween(user, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public TransactionResponse getTransactionById(Long id) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToResponse(transaction);
    }
    
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        transaction.setName(request.getName());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.valueOf(request.getType().toUpperCase()));
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        
        Transaction updated = transactionRepository.save(transaction);
        
        // Update budget spent
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpent(user, request.getCategory(), 
                    request.getDate().getMonthValue(), request.getDate().getYear());
        }
        
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteTransaction(Long id) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        transactionRepository.delete(transaction);
    }
    
    public Map<String, BigDecimal> getExpensesByCategory() {
        User user = getCurrentUser();
        List<Object[]> results = transactionRepository.sumAmountByUserAndTypeGroupByCategory(
                user, TransactionType.EXPENSE);
        
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        for (Object[] result : results) {
            categoryMap.put((String) result[0], (BigDecimal) result[1]);
        }
        return categoryMap;
    }
    
    public Map<String, BigDecimal> getIncomeByCategory() {
        User user = getCurrentUser();
        List<Object[]> results = transactionRepository.sumAmountByUserAndTypeGroupByCategory(
                user, TransactionType.INCOME);
        
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        for (Object[] result : results) {
            categoryMap.put((String) result[0], (BigDecimal) result[1]);
        }
        return categoryMap;
    }
    
    public BigDecimal getTotalIncome() {
        User user = getCurrentUser();
        BigDecimal total = transactionRepository.sumAmountByUserAndType(user, TransactionType.INCOME);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalExpenses() {
        User user = getCurrentUser();
        BigDecimal total = transactionRepository.sumAmountByUserAndType(user, TransactionType.EXPENSE);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setName(transaction.getName());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType().name());
        response.setCategory(transaction.getCategory());
        response.setDate(transaction.getDate());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
