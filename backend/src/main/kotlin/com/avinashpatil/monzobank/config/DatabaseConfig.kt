package com.avinashpatil.monzobank.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Database configuration for JPA, Hibernate, and transaction management
 * 
 * This configuration sets up the database connection pool, entity manager factory,
 * and transaction manager with optimized settings for banking operations.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = ["com.avinashpatil.monzobank.repository"]
)
@EnableJpaAuditing
@EnableTransactionManagement
class DatabaseConfig {

    @Value("\${spring.datasource.url}")
    private lateinit var databaseUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var databaseUsername: String

    @Value("\${spring.datasource.password}")
    private lateinit var databasePassword: String

    @Value("\${spring.datasource.driver-class-name}")
    private lateinit var databaseDriverClassName: String

    /**
     * Primary data source with HikariCP connection pool
     */
    @Bean
    @Primary
    fun dataSource(): DataSource {
        val config = HikariConfig()
        
        // Basic connection settings
        config.jdbcUrl = databaseUrl
        config.username = databaseUsername
        config.password = databasePassword
        config.driverClassName = databaseDriverClassName
        
        // Connection pool settings
        config.maximumPoolSize = 20
        config.minimumIdle = 5
        config.idleTimeout = 300000 // 5 minutes
        config.connectionTimeout = 20000 // 20 seconds
        config.leakDetectionThreshold = 60000 // 1 minute
        config.maxLifetime = 1800000 // 30 minutes
        
        // Performance settings
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        
        // Connection validation
        config.connectionTestQuery = "SELECT 1"
        config.validationTimeout = 5000
        
        // Pool name for monitoring
        config.poolName = "MonzoBank-HikariCP"
        
        // Additional properties for PostgreSQL
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.addDataSourceProperty("useServerPrepStmts", "true")
        config.addDataSourceProperty("rewriteBatchedStatements", "true")
        config.addDataSourceProperty("cacheResultSetMetadata", "true")
        config.addDataSourceProperty("cacheServerConfiguration", "true")
        config.addDataSourceProperty("elideSetAutoCommits", "true")
        config.addDataSourceProperty("maintainTimeStats", "false")
        
        return HikariDataSource(config)
    }

    /**
     * Entity manager factory with Hibernate configuration
     */
    @Bean
    @Primary
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactory = LocalContainerEntityManagerFactoryBean()
        entityManagerFactory.dataSource = dataSource
        entityManagerFactory.setPackagesToScan("com.avinashpatil.monzobank.entity")
        
        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setGenerateDdl(false)
        vendorAdapter.setShowSql(false)
        entityManagerFactory.jpaVendorAdapter = vendorAdapter
        
        val properties = Properties()
        
        // Hibernate dialect and basic settings
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        properties.setProperty("hibernate.hbm2ddl.auto", "validate")
        properties.setProperty("hibernate.show_sql", "false")
        properties.setProperty("hibernate.format_sql", "true")
        properties.setProperty("hibernate.use_sql_comments", "true")
        
        // Performance optimizations
        properties.setProperty("hibernate.jdbc.batch_size", "20")
        properties.setProperty("hibernate.jdbc.fetch_size", "50")
        properties.setProperty("hibernate.order_inserts", "true")
        properties.setProperty("hibernate.order_updates", "true")
        properties.setProperty("hibernate.batch_versioned_data", "true")
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true")
        
        // Connection and transaction settings
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true")
        properties.setProperty("hibernate.connection.autocommit", "false")
        
        // Cache settings
        properties.setProperty("hibernate.cache.use_second_level_cache", "true")
        properties.setProperty("hibernate.cache.use_query_cache", "true")
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory")
        
        // Statistics and monitoring
        properties.setProperty("hibernate.generate_statistics", "true")
        properties.setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", "1000")
        
        // Naming strategy
        properties.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
        
        // Time zone settings
        properties.setProperty("hibernate.jdbc.time_zone", "UTC")
        
        entityManagerFactory.setJpaProperties(properties)
        
        return entityManagerFactory
    }

    /**
     * Transaction manager for JPA transactions
     */
    @Bean
    @Primary
    fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        
        // Transaction timeout settings
        transactionManager.defaultTimeout = 30 // 30 seconds
        
        return transactionManager
    }

    /**
     * Read-only data source for reporting and analytics queries
     * This can be configured to point to a read replica in production
     */
    @Bean(name = ["readOnlyDataSource"])
    fun readOnlyDataSource(): DataSource {
        val config = HikariConfig()
        
        // Use the same database for now, but this can be changed to a read replica
        config.jdbcUrl = databaseUrl
        config.username = databaseUsername
        config.password = databasePassword
        config.driverClassName = databaseDriverClassName
        
        // Smaller connection pool for read-only operations
        config.maximumPoolSize = 10
        config.minimumIdle = 2
        config.idleTimeout = 600000 // 10 minutes
        config.connectionTimeout = 30000 // 30 seconds
        config.maxLifetime = 1800000 // 30 minutes
        
        // Read-only settings
        config.isReadOnly = true
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        
        config.poolName = "MonzoBank-ReadOnly-HikariCP"
        
        return HikariDataSource(config)
    }

    /**
     * Entity manager factory for read-only operations
     */
    @Bean(name = ["readOnlyEntityManagerFactory"])
    fun readOnlyEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactory = LocalContainerEntityManagerFactoryBean()
        entityManagerFactory.dataSource = readOnlyDataSource()
        entityManagerFactory.setPackagesToScan("com.avinashpatil.monzobank.entity")
        
        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setGenerateDdl(false)
        vendorAdapter.setShowSql(false)
        entityManagerFactory.jpaVendorAdapter = vendorAdapter
        
        val properties = Properties()
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        properties.setProperty("hibernate.hbm2ddl.auto", "validate")
        properties.setProperty("hibernate.show_sql", "false")
        properties.setProperty("hibernate.connection.autocommit", "true")
        properties.setProperty("hibernate.default_readonly", "true")
        
        entityManagerFactory.setJpaProperties(properties)
        
        return entityManagerFactory
    }
}