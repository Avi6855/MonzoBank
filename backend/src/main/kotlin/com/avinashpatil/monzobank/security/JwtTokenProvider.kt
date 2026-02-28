package com.avinashpatil.monzobank.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider {
    
    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecret: String
    
    @Value("\${app.jwt.expiration}")
    private var jwtExpiration: Long = 0
    
    @Value("\${app.jwt.refresh-expiration}")
    private var refreshExpiration: Long = 0
    
    private val key: Key by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }
    
    fun generateAccessToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)
        
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "access")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun generateRefreshToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshExpiration)
        
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "refresh")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun generatePasswordResetToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 3600000) // 1 hour
        
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "password_reset")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun generateEmailVerificationToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 86400000) // 24 hours
        
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "email_verification")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun getUserIdFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        
        return claims.subject
    }
    
    fun getTokenType(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims["type"] as? String
        } catch (e: Exception) {
            null
        }
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims.expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }
    
    fun getExpirationDateFromToken(token: String): Date? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims.expiration
        } catch (e: Exception) {
            null
        }
    }
    
    fun getAccessTokenExpiration(): Long {
        return jwtExpiration / 1000 // Return in seconds
    }
    
    fun getRefreshTokenExpiration(): Long {
        return refreshExpiration / 1000 // Return in seconds
    }
    
    fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }
}