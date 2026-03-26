package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    
    @PostMapping("/add")
    public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("Error: Amount must be greater than zero.");
        }
        if (transaction.getUserId() == null) {
            return ResponseEntity.badRequest().body("Error: User ID is required.");
        }

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long userId) {
        List<Transaction> list = transactionRepository.findByUserIdOrderByDateDesc(userId);
        return ResponseEntity.ok(list);
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok("Transaction deleted successfully.");
        }
        return ResponseEntity.status(404).body("Error: Transaction not found.");
    }

    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTxn) {
        Optional<Transaction> existing = transactionRepository.findById(id);

        if (existing.isPresent()) {
            Transaction t = existing.get();
            t.setAmount(updatedTxn.getAmount());
            t.setDescription(updatedTxn.getDescription());
            t.setCategory(updatedTxn.getCategory());
            t.setType(updatedTxn.getType());
            // Keep original date or update to new one if provided
            if (updatedTxn.getDate() != null) t.setDate(updatedTxn.getDate());

            return ResponseEntity.ok(transactionRepository.save(t));
        }
        return ResponseEntity.status(404).body("Error: Transaction not found.");
    }
}