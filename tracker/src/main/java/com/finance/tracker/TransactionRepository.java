package com.finance.tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT new com.finance.tracker.BudgetSummaryDTO(t.category, SUM(t.amount)) " +
           "FROM Transaction t " +
           "WHERE t.userId = :userId " +
           "AND UPPER(t.type) = 'EXPENSE' " +
           "AND MONTH(t.date) = MONTH(CURRENT_DATE) " +
           "AND YEAR(t.date) = YEAR(CURRENT_DATE) " +
           "GROUP BY t.category")
    List<BudgetSummaryDTO> findSpendingByCategoryForCurrentMonth(@Param("userId") Long userId);

    
    @Query("SELECT new com.finance.tracker.MonthlyTrendDTO(MONTHNAME(t.date), SUM(t.amount)) " +
           "FROM Transaction t " +
           "WHERE t.userId = :userId " +
           "AND (UPPER(t.type) = 'EXPENSE' OR UPPER(t.type) = 'EXNENSE') " + 
           "GROUP BY MONTHNAME(t.date), MONTH(t.date) " +
           "ORDER BY MONTH(t.date) ASC")
    List<MonthlyTrendDTO> getMonthlySpendingTrend(@Param("userId") Long userId);
}