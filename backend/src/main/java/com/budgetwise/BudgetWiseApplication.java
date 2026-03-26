package com.budgetwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BudgetWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetWiseApplication.class, args);
        System.out.println("BudgetWise Application Started Successfully!");
    }
}
