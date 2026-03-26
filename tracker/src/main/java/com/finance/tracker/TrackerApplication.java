package com.finance.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class TrackerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TrackerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TrackerApplication.class, args);
        
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port");
        if (port == null) port = "8080"; 

        System.out.println("\n----------------------------------------------------------");
        System.out.println("   BUDGETWISE AI-ADVISOR SYSTEM IS LIVE!");
        System.out.println("   Local Access: http://localhost:" + port + "/login.html");
        System.out.println("   Database: Oracle MySQL connected successfully.");
        System.out.println("----------------------------------------------------------\n");

        logger.info("Application started successfully on port {}", port);
    }
}