package com.budgetwise.controller;

import com.budgetwise.dto.TransactionRequest;
import com.budgetwise.dto.TransactionResponse;
import com.budgetwise.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().body(Map.of("message", "Transaction deleted successfully"));
    }
    
    @GetMapping("/expenses-by-category")
    public ResponseEntity<Map<String, BigDecimal>> getExpensesByCategory() {
        Map<String, BigDecimal> expenses = transactionService.getExpensesByCategory();
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/income-by-category")
    public ResponseEntity<Map<String, BigDecimal>> getIncomeByCategory() {
        Map<String, BigDecimal> income = transactionService.getIncomeByCategory();
        return ResponseEntity.ok(income);
    }
    
    @GetMapping("/total-income")
    public ResponseEntity<Map<String, BigDecimal>> getTotalIncome() {
        BigDecimal total = transactionService.getTotalIncome();
        return ResponseEntity.ok(Map.of("totalIncome", total));
    }
    
    @GetMapping("/total-expenses")
    public ResponseEntity<Map<String, BigDecimal>> getTotalExpenses() {
        BigDecimal total = transactionService.getTotalExpenses();
        return ResponseEntity.ok(Map.of("totalExpenses", total));
    }
}
