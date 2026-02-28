package com.avinashpatil.monzobank.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    
    private val logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)
    private val objectMapper = ObjectMapper()
    
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Unauthorized error: {}", authException.message)
        
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        
        val errorResponse = mapOf(
            "success" to false,
            "message" to "Unauthorized: Authentication token is required",
            "error" to authException.message,
            "timestamp" to LocalDateTime.now().toString(),
            "path" to request.requestURI,
            "status" to HttpServletResponse.SC_UNAUTHORIZED
        )
        
        val outputStream = response.outputStream
        objectMapper.writeValue(outputStream, errorResponse)
        outputStream.flush()
    }
}