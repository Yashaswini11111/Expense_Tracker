package com.budgetwise.service;

import com.budgetwise.model.Transaction;
import com.budgetwise.model.User;
import com.budgetwise.repository.TransactionRepository;
import com.budgetwise.repository.UserRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportService {
    
   @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public byte[] exportTransactionsToCSV() {
        try {
            User user = getCurrentUser();
            List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVWriter writer = new CSVWriter(outputStreamWriter);
            
            // Write header
            String[] header = {"ID", "Name", "Amount", "Type", "Category", "Date", "Description"};
            writer.writeNext(header);
            
            // Write data
            for (Transaction transaction : transactions) {
                String[] data = {
                    transaction.getId().toString(),
                    transaction.getName(),
                    transaction.getAmount().toString(),
                    transaction.getType().toString(),
                    transaction.getCategory(),
                    transaction.getDate().toString(),
                    transaction.getDescription() != null ? transaction.getDescription() : ""
                };
                writer.writeNext(data);
            }
            
            writer.close();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to CSV: " + e.getMessage());
        }
    }
}
