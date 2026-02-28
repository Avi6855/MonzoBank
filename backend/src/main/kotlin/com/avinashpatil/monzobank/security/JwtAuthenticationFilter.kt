package com.avinashpatil.monzobank.security

import com.avinashpatil.monzobank.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) : OncePerRequestFilter() {
    
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt!!)) {
                val userId = jwtTokenProvider.getUserIdFromToken(jwt)
                val tokenType = jwtTokenProvider.getTokenTypeFromToken(jwt)
                
                // Only allow access tokens for API authentication
                if (tokenType == "ACCESS") {
                    try {
                        val user = userService.getUserById(UUID.fromString(userId))
                        
                        // Create authorities based on user role
                        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role ?: "USER""))
                        
                        val authentication = UsernamePasswordAuthenticationToken(
                            userId, // Use userId as principal
                            null,
                            authorities
                        )
                        
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                        
                        logger.debug("Set authentication for user: {}", userId)
                        
                    } catch (e: Exception) {
                        logger.error("Cannot set user authentication: {}", e.message)
                        SecurityContextHolder.clearContext()
                    }
                } else {
                    logger.warn("Invalid token type for API access: {}", tokenType)
                }
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e.message)
            SecurityContextHolder.clearContext()
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
    
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        
        // Skip JWT filter for public endpoints
        val publicPaths = listOf(
            "/auth/",
            "/actuator/",
            "/health",
            "/info",
            "/metrics",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars/",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/error"
        )
        
        return publicPaths.any { path.startsWith(it) }
    }
}