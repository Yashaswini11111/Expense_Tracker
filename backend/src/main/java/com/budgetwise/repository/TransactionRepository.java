package com.budgetwise.repository;

import com.budgetwise.model.Transaction;
import com.budgetwise.model.Transaction.TransactionType;
import com.budgetwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndType(User user, TransactionType type);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserAndCategory(User user, String category);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type")
    BigDecimal sumAmountByUserAndType(@Param("user") User user, @Param("type") TransactionType type);
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type GROUP BY t.category")
    List<Object[]> sumAmountByUserAndTypeGroupByCategory(@Param("user") User user, @Param("type") TransactionType type);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.category = :category AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumAmountByUserAndTypeAndCategoryAndMonthAndYear(
        @Param("user") User user,
        @Param("type") TransactionType type,
        @Param("category") String category,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}
