package com.budgetwise.controller;

import com.budgetwise.dto.SavingsGoalRequest;
import com.budgetwise.dto.SavingsGoalResponse;
import com.budgetwise.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/savings-goals")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavingsGoalController {
    
    @Autowired
    private SavingsGoalService savingsGoalService;
    
    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createSavingsGoal(@Valid @RequestBody SavingsGoalRequest request) {
        SavingsGoalResponse response = savingsGoalService.createSavingsGoal(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getAllSavingsGoals() {
        List<SavingsGoalResponse> goals = savingsGoalService.getAllSavingsGoals();
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<SavingsGoalResponse>> getActiveSavingsGoals() {
        List<SavingsGoalResponse> goals = savingsGoalService.getActiveSavingsGoals();
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getSavingsGoalById(@PathVariable Long id) {
        SavingsGoalResponse goal = savingsGoalService.getSavingsGoalById(id);
        return ResponseEntity.ok(goal);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateSavingsGoal(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalRequest request) {
        SavingsGoalResponse response = savingsGoalService.updateSavingsGoal(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/progress")
    public ResponseEntity<SavingsGoalResponse> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        SavingsGoalResponse response = savingsGoalService.updateProgress(id, amount);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingsGoal(@PathVariable Long id) {
        savingsGoalService.deleteSavingsGoal(id);
        return ResponseEntity.ok().body(Map.of("message", "Savings goal deleted successfully"));
    }
}
