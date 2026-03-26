package com.finance.tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    
    List<SavingsGoal> findByUserId(Long userId);

    List<SavingsGoal> findByUserIdOrderByDeadlineAsc(Long userId);

    
    @Query("SELECT s FROM SavingsGoal s WHERE s.userId = :userId AND s.currentAmount < s.targetAmount")
    List<SavingsGoal> findActiveGoals(@Param("userId") Long userId);

    
    @Query("SELECT s FROM SavingsGoal s WHERE s.userId = :userId AND s.currentAmount >= s.targetAmount")
    List<SavingsGoal> findCompletedGoals(@Param("userId") Long userId);
}