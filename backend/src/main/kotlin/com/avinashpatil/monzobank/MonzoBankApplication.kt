package com.avinashpatil.monzobank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Main application class for MonzoBank API
 * 
 * This class serves as the entry point for the Spring Boot application.
 * It enables various Spring features including:
 * - Caching for improved performance
 * - Async processing for non-blocking operations
 * - Scheduling for background tasks
 * - Transaction management for database operations
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
class MonzoBankApplication

/**
 * Main function to start the MonzoBank application
 * 
 * @param args Command line arguments
 */
fun main(args: Array<String>) {
    runApplication<MonzoBankApplication>(*args)
}