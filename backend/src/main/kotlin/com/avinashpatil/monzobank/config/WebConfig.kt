package com.avinashpatil.monzobank.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.*
import java.time.format.DateTimeFormatter

/**
 * Web configuration for CORS, interceptors, and HTTP message converters
 * 
 * This configuration sets up web-related settings including CORS policies,
 * JSON serialization, and request/response handling.
 */
@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    @Value("\${app.security.cors.allowed-origins}")
    private lateinit var allowedOrigins: String

    @Value("\${app.security.cors.allowed-methods}")
    private lateinit var allowedMethods: String

    @Value("\${app.security.cors.allowed-headers}")
    private lateinit var allowedHeaders: String

    @Value("\${app.security.cors.allow-credentials}")
    private var allowCredentials: Boolean = true

    @Value("\${app.security.cors.max-age}")
    private var maxAge: Long = 3600

    /**
     * CORS configuration for cross-origin requests
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins.split(",").toTypedArray())
            .allowedMethods(*allowedMethods.split(",").toTypedArray())
            .allowedHeaders(*allowedHeaders.split(",").toTypedArray())
            .allowCredentials(allowCredentials)
            .maxAge(maxAge)
    }

    /**
     * Configure path matching for case-insensitive URLs
     */
    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.setUseTrailingSlashMatch(true)
        configurer.setUseSuffixPatternMatch(false)
    }

    /**
     * Configure content negotiation
     */
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer
            .favorParameter(false)
            .favorPathExtension(false)
            .ignoreAcceptHeader(false)
            .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
    }

    /**
     * Configure HTTP message converters
     */
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(mappingJackson2HttpMessageConverter())
    }

    /**
     * Custom Jackson HTTP message converter with banking-specific configurations
     */
    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = objectMapper()
        return converter
    }

    /**
     * Custom ObjectMapper with banking-specific configurations
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        
        // Register modules
        mapper.registerModule(KotlinModule.Builder().build())
        mapper.registerModule(JavaTimeModule())
        
        // Configure serialization
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        
        // Configure deserialization
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        
        // Configure property inclusion
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        
        // Configure date formats
        mapper.setDateFormat(java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        
        return mapper
    }

    /**
     * Add resource handlers for static content
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
        
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:./uploads/")
            .setCachePeriod(3600)
        
        // Swagger UI resources
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
        
        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

    /**
     * Add interceptors for request/response processing
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        // Request logging interceptor
        registry.addInterceptor(RequestLoggingInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/health", "/api/actuator/**")
        
        // Rate limiting interceptor
        registry.addInterceptor(RateLimitingInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/health", "/api/actuator/**")
        
        // Security headers interceptor
        registry.addInterceptor(SecurityHeadersInterceptor())
            .addPathPatterns("/api/**")
    }

    /**
     * Configure view resolvers
     */
    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.enableContentNegotiation()
    }

    /**
     * Configure default servlet handling
     */
    override fun configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer) {
        configurer.enable()
    }

    /**
     * Request logging interceptor for audit and debugging
     */
    class RequestLoggingInterceptor : org.springframework.web.servlet.HandlerInterceptor {
        
        override fun preHandle(
            request: javax.servlet.http.HttpServletRequest,
            response: javax.servlet.http.HttpServletResponse,
            handler: Any
        ): Boolean {
            val startTime = System.currentTimeMillis()
            request.setAttribute("startTime", startTime)
            
            // Log request details (be careful not to log sensitive data)
            val method = request.method
            val uri = request.requestURI
            val userAgent = request.getHeader("User-Agent")
            val clientIp = getClientIpAddress(request)
            
            println("[REQUEST] $method $uri from $clientIp - User-Agent: $userAgent")
            
            return true
        }
        
        override fun afterCompletion(
            request: javax.servlet.http.HttpServletRequest,
            response: javax.servlet.http.HttpServletResponse,
            handler: Any,
            ex: Exception?
        ) {
            val startTime = request.getAttribute("startTime") as? Long ?: return
            val duration = System.currentTimeMillis() - startTime
            
            val method = request.method
            val uri = request.requestURI
            val status = response.status
            
            println("[RESPONSE] $method $uri - Status: $status - Duration: ${duration}ms")
            
            if (ex != null) {
                println("[ERROR] Exception in request processing: ${ex.message}")
            }
        }
        
        private fun getClientIpAddress(request: javax.servlet.http.HttpServletRequest): String {
            val xForwardedFor = request.getHeader("X-Forwarded-For")
            if (!xForwardedFor.isNullOrBlank()) {
                return xForwardedFor.split(",")[0].trim()
            }
            
            val xRealIp = request.getHeader("X-Real-IP")
            if (!xRealIp.isNullOrBlank()) {
                return xRealIp
            }
            
            return request.remoteAddr
        }
    }

    /**
     * Rate limiting interceptor to prevent abuse
     */
    class RateLimitingInterceptor : org.springframework.web.servlet.HandlerInterceptor {
        
        // Simple in-memory rate limiting (use Redis in production)
        private val requestCounts = mutableMapOf<String, MutableList<Long>>()
        private val maxRequestsPerMinute = 100
        
        override fun preHandle(
            request: javax.servlet.http.HttpServletRequest,
            response: javax.servlet.http.HttpServletResponse,
            handler: Any
        ): Boolean {
            val clientIp = getClientIpAddress(request)
            val currentTime = System.currentTimeMillis()
            val oneMinuteAgo = currentTime - 60000
            
            // Clean old requests
            val requests = requestCounts.getOrPut(clientIp) { mutableListOf() }
            requests.removeAll { it < oneMinuteAgo }
            
            // Check rate limit
            if (requests.size >= maxRequestsPerMinute) {
                response.status = 429 // Too Many Requests
                response.setHeader("Retry-After", "60")
                response.writer.write("{\"error\":\"Rate limit exceeded\"}")
                return false
            }
            
            // Add current request
            requests.add(currentTime)
            
            return true
        }
        
        private fun getClientIpAddress(request: javax.servlet.http.HttpServletRequest): String {
            val xForwardedFor = request.getHeader("X-Forwarded-For")
            if (!xForwardedFor.isNullOrBlank()) {
                return xForwardedFor.split(",")[0].trim()
            }
            
            val xRealIp = request.getHeader("X-Real-IP")
            if (!xRealIp.isNullOrBlank()) {
                return xRealIp
            }
            
            return request.remoteAddr
        }
    }

    /**
     * Security headers interceptor to add security-related HTTP headers
     */
    class SecurityHeadersInterceptor : org.springframework.web.servlet.HandlerInterceptor {
        
        override fun postHandle(
            request: javax.servlet.http.HttpServletRequest,
            response: javax.servlet.http.HttpServletResponse,
            handler: Any,
            modelAndView: org.springframework.web.servlet.ModelAndView?
        ) {
            // Add security headers
            response.setHeader("X-Content-Type-Options", "nosniff")
            response.setHeader("X-Frame-Options", "DENY")
            response.setHeader("X-XSS-Protection", "1; mode=block")
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin")
            response.setHeader("Content-Security-Policy", "default-src 'self'")
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        }
    }
}