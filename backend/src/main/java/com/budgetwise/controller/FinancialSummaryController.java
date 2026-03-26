package com.budgetwise.controller;

import com.budgetwise.dto.FinancialSummaryResponse;
import com.budgetwise.service.FinancialSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinancialSummaryController {
    
    @Autowired
    private FinancialSummaryService financialSummaryService;
    
    @GetMapping
    public ResponseEntity<FinancialSummaryResponse> getFinancialSummary() {
        FinancialSummaryResponse summary = financialSummaryService.getFinancialSummary();
        return ResponseEntity.ok(summary);
    }
}
