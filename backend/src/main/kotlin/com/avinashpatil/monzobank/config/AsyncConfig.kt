package com.avinashpatil.monzobank.config

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * Async configuration for handling asynchronous operations
 * 
 * This configuration sets up thread pools for different types of async operations
 * such as notifications, file processing, and background tasks.
 */
@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {

    /**
     * Default async executor for general async operations
     */
    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 20
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("MonzoBank-Async-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * Notification executor for sending emails, SMS, and push notifications
     */
    @Bean(name = ["notificationExecutor"])
    fun notificationExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 10
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("MonzoBank-Notification-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * File processing executor for handling file uploads, exports, and processing
     */
    @Bean(name = ["fileProcessingExecutor"])
    fun fileProcessingExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 5
        executor.queueCapacity = 25
        executor.setThreadNamePrefix("MonzoBank-FileProcessing-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(60)
        executor.initialize()
        return executor
    }

    /**
     * Analytics executor for processing analytics and reporting tasks
     */
    @Bean(name = ["analyticsExecutor"])
    fun analyticsExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 8
        executor.queueCapacity = 30
        executor.setThreadNamePrefix("MonzoBank-Analytics-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(45)
        executor.initialize()
        return executor
    }

    /**
     * External API executor for calling external services
     */
    @Bean(name = ["externalApiExecutor"])
    fun externalApiExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 15
        executor.queueCapacity = 40
        executor.setThreadNamePrefix("MonzoBank-ExternalAPI-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * Fraud detection executor for processing fraud detection tasks
     */
    @Bean(name = ["fraudDetectionExecutor"])
    fun fraudDetectionExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 6
        executor.queueCapacity = 20
        executor.setThreadNamePrefix("MonzoBank-FraudDetection-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * Investment processing executor for handling investment-related operations
     */
    @Bean(name = ["investmentExecutor"])
    fun investmentExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 8
        executor.queueCapacity = 30
        executor.setThreadNamePrefix("MonzoBank-Investment-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * Loan processing executor for handling loan-related operations
     */
    @Bean(name = ["loanExecutor"])
    fun loanExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 6
        executor.queueCapacity = 25
        executor.setThreadNamePrefix("MonzoBank-Loan-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }

    /**
     * Exception handler for async operations
     */
    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { throwable, method, params ->
            val paramString = params.joinToString(", ") { it?.toString() ?: "null" }
            println("Async execution error in method ${method.name} with params [$paramString]: ${throwable.message}")
            throwable.printStackTrace()
            
            // Here you could also send notifications to monitoring systems
            // or log to external logging