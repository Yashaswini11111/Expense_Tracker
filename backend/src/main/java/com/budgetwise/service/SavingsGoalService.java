package com.budgetwise.service;

import com.budgetwise.dto.SavingsGoalRequest;
import com.budgetwise.dto.SavingsGoalResponse;
import com.budgetwise.model.SavingsGoal;
import com.budgetwise.model.User;
import com.budgetwise.repository.SavingsGoalRepository;
import com.budgetwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {
    
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public SavingsGoalResponse createSavingsGoal(SavingsGoalRequest request) {
        User user = getCurrentUser();
        
        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setAchieved(false);
        
        SavingsGoal saved = savingsGoalRepository.save(goal);
        return mapToResponse(saved);
    }
    
    public List<SavingsGoalResponse> getAllSavingsGoals() {
        User user = getCurrentUser();
        return savingsGoalRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SavingsGoalResponse> getActiveSavingsGoals() {
        User user = getCurrentUser();
        return savingsGoalRepository.findByUserAndAchieved(user, false).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public SavingsGoalResponse getSavingsGoalById(Long id) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToResponse(goal);
    }
    
    @Transactional
    public SavingsGoalResponse updateSavingsGoal(Long id, SavingsGoalRequest request) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        
        // Check if achieved
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setAchieved(true);
        }
        
        SavingsGoal updated = savingsGoalRepository.save(goal);
        return mapToResponse(updated);
    }
    
    @Transactional
    public SavingsGoalResponse updateProgress(Long id, BigDecimal amount) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        goal.setCurrentAmount(amount);
        
        // Check if achieved
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setAchieved(true);
        }
        
        SavingsGoal updated = savingsGoalRepository.save(goal);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteSavingsGoal(Long id) {
        User user = getCurrentUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        savingsGoalRepository.delete(goal);
    }
    
    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
        SavingsGoalResponse response = new SavingsGoalResponse();
        response.setId(goal.getId());
        response.setName(goal.getName());
        response.setTargetAmount(goal.getTargetAmount());
        response.setCurrentAmount(goal.getCurrentAmount());
        response.setRemaining(goal.getTargetAmount().subtract(goal.getCurrentAmount()));
        response.setTargetDate(goal.getTargetDate());
        response.setDescription(goal.getDescription());
        response.setAchieved(goal.getAchieved());
        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        
        // Calculate percentage achieved
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            double percentage = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            response.setPercentageAchieved(Math.min(percentage, 100.0));
        } else {
            response.setPercentageAchieved(0.0);
        }
        
        return response;
    }
}
