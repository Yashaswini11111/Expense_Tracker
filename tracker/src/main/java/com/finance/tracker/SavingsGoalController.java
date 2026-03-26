package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/savings")
@CrossOrigin(origins = "*")
public class SavingsGoalController {

    @Autowired 
    private SavingsGoalRepository repo;

    @PostMapping("/add")
    public ResponseEntity<?> addGoal(@RequestBody SavingsGoal goal) {
        if (goal.getUserId() == null || goal.getGoalName() == null || goal.getTargetAmount() <= 0) {
            return ResponseEntity.badRequest().body("Invalid goal data. Please provide name and target amount.");
        }
        
        if (goal.getCurrentAmount() == null) goal.setCurrentAmount(0.0);
        
        SavingsGoal savedGoal = repo.save(goal);
        return ResponseEntity.ok(savedGoal);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsGoal>> getGoals(@PathVariable Long userId) {
        List<SavingsGoal> goals = repo.findByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    
    @PutMapping("/update-progress/{goalId}")
    public ResponseEntity<?> addSavings(@PathVariable Long goalId, @RequestParam Double amount) {
        Optional<SavingsGoal> goalOpt = repo.findById(goalId);
        
        if (goalOpt.isPresent()) {
            SavingsGoal goal = goalOpt.get();
            double newAmount = goal.getCurrentAmount() + amount;
            goal.setCurrentAmount(newAmount);
            
            repo.save(goal);
            
            if (newAmount >= goal.getTargetAmount()) {
                return ResponseEntity.ok("CONGRATS! You have reached your goal: " + goal.getGoalName());
            }
            
            return ResponseEntity.ok(goal);
        } else {
            return ResponseEntity.status(404).body("Goal not found.");
        }
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok("Goal deleted successfully.");
        }
        return ResponseEntity.status(404).body("Goal not found.");
    }
}