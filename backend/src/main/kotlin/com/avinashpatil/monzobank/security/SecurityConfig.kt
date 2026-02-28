package com.avinashpatil.monzobank.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers(
                        "/auth/register",
                        "/auth/login",
                        "/auth/refresh-token",
                        "/auth/forgot-password",
                        "/auth/reset-password",
                        "/auth/verify-email/**",
                        "/auth/resend-verification-email",
                        "/auth/resend-verification-sms"
                    ).permitAll()
                    
                    // Health check and actuator endpoints
                    .requestMatchers(
                        "/actuator/**",
                        "/health",
                        "/info",
                        "/metrics"
                    ).permitAll()
                    
                    // API documentation endpoints
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    
                    // Static resources
                    .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                    ).permitAll()
                    
                    // Error endpoints
                    .requestMatchers("/error").permitAll()
                    
                    // Public market data endpoints (read-only)
                    .requestMatchers(HttpMethod.GET, "/investments/market-data/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/investments/news").permitAll()
                    .requestMatchers(HttpMethod.GET, "/investments/education").permitAll()
                    .requestMatchers(HttpMethod.POST, "/investments/backtest").permitAll()
                    .requestMatchers(HttpMethod.POST, "/loans/calculator").permitAll()
                    .requestMatchers(HttpMethod.POST, "/loans/compare").permitAll()
                    .requestMatchers(HttpMethod.GET, "/pots/templates").permitAll()
                    
                    // Admin endpoints - require ADMIN role
                    .requestMatchers(
                        "/admin/**",
                        "/loans/*/approve",
                        "/loans/*/reject",
                        "/loans/*/disburse",
                        "/investments/update-prices"
                    ).hasRole("ADMIN")
                    
                    // User management endpoints - require USER role or higher
                    .requestMatchers(
                        "/users/**",
                        "/accounts/**",
                        "/transactions/**",
                        "/cards/**",
                        "/pots/**",
                        "/investments/**",
                        "/loans/**"
                    ).hasAnyRole("USER", "ADMIN")
                    
                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }
}