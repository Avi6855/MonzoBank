package com.avinashpatil.app.monzobank.utils

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

/**
 * Local unit tests for [SecurityUtils] — pure-JVM security helpers used by the
 * MonzoBank fintech flows (PIN, amounts, transfer limits, fraud heuristics).
 *
 * Only JVM-safe members are covered here. Anything touching
 * `android.util.*` (e.g. [SecurityUtils.isValidEmail] via
 * `Patterns.EMAIL_ADDRESS`) belongs in an instrumented / Robolectric test.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class SecurityUtilsTest {

    // ---- PIN ----

    @Test
    fun `isValidPin accepts 4 to 6 digits`() {
        assertTrue(SecurityUtils.isValidPin("1234"))
        assertTrue(SecurityUtils.isValidPin("123456"))
    }

    @Test
    fun `isValidPin rejects malformed pins`() {
        assertFalse(SecurityUtils.isValidPin(""))
        assertFalse(SecurityUtils.isValidPin("123"))
        assertFalse(SecurityUtils.isValidPin("1234567"))
        assertFalse(SecurityUtils.isValidPin("12a4"))
    }

    // ---- Amount ----

    @Test
    fun `isValidAmount accepts positive values with up to 2 decimals`() {
        assertTrue(SecurityUtils.isValidAmount("10"))
        assertTrue(SecurityUtils.isValidAmount("10.5"))
        assertTrue(SecurityUtils.isValidAmount("10.50"))
    }

    @Test
    fun `isValidAmount rejects zero negative and 3-decimal values`() {
        assertFalse(SecurityUtils.isValidAmount("0"))
        assertFalse(SecurityUtils.isValidAmount("-5"))
        assertFalse(SecurityUtils.isValidAmount("10.123"))
        assertFalse(SecurityUtils.isValidAmount("abc"))
        assertFalse(SecurityUtils.isValidAmount(""))
    }

    // ---- Transfer limits ----

    @Test
    fun `isValidTransferAmount accepts amount within balance`() {
        val result = SecurityUtils.isValidTransferAmount(
            amount = BigDecimal("50"),
            accountBalance = BigDecimal("100"),
            overdraftLimit = null,
            dailyLimit = null,
            monthlyLimit = null
        )
        assertTrue(result.isValid)
    }

    @Test
    fun `isValidTransferAmount rejects amount above available balance`() {
        val result = SecurityUtils.isValidTransferAmount(
            amount = BigDecimal("200"),
            accountBalance = BigDecimal("100"),
            overdraftLimit = null,
            dailyLimit = null,
            monthlyLimit = null
        )
        assertFalse(result.isValid)
        assertEquals("Insufficient funds", result.message)
    }

    @Test
    fun `isValidTransferAmount includes overdraft in available balance`() {
        val result = SecurityUtils.isValidTransferAmount(
            amount = BigDecimal("140"),
            accountBalance = BigDecimal("100"),
            overdraftLimit = BigDecimal("50"),
            dailyLimit = null,
            monthlyLimit = null
        )
        assertTrue(result.isValid)
    }

    @Test
    fun `isValidTransferAmount enforces daily limit`() {
        val result = SecurityUtils.isValidTransferAmount(
            amount = BigDecimal("500"),
            accountBalance = BigDecimal("1000"),
            overdraftLimit = null,
            dailyLimit = BigDecimal("100"),
            monthlyLimit = null
        )
        assertFalse(result.isValid)
        assertTrue(result.message.startsWith("Amount exceeds daily limit"))
    }

    // ---- Fraud heuristic ----

    @Test
    fun `isSuspiciousTransaction flags 5x above-average amounts`() {
        assertTrue(
            SecurityUtils.isSuspiciousTransaction(
                amount = BigDecimal("600"),
                averageTransactionAmount = BigDecimal("100")
            )
        )
        assertFalse(
            SecurityUtils.isSuspiciousTransaction(
                amount = BigDecimal("200"),
                averageTransactionAmount = BigDecimal("100")
            )
        )
    }

    @Test
    fun `isSuspiciousTransaction flags large international and unusual-time payments`() {
        assertTrue(
            SecurityUtils.isSuspiciousTransaction(
                amount = BigDecimal("1500"),
                averageTransactionAmount = BigDecimal("100"),
                isInternational = true
            )
        )
        assertTrue(
            SecurityUtils.isSuspiciousTransaction(
                amount = BigDecimal("600"),
                averageTransactionAmount = BigDecimal("100"),
                isUnusualTime = true
            )
        )
    }

    // ---- Transaction reference ----

    @Test
    fun `generateTransactionReference produces valid 8-char references`() {
        val ref = SecurityUtils.generateTransactionReference()
        assertEquals(8, ref.length)
        assertTrue(SecurityUtils.isValidTransactionReference(ref))
    }

    @Test
    fun `isValidTransactionReference rejects malformed references`() {
        assertFalse(SecurityUtils.isValidTransactionReference(""))
        assertFalse(SecurityUtils.isValidTransactionReference("abc"))
        assertFalse(SecurityUtils.isValidTransactionReference("TOOLONGREF123"))
    }

    // ---- Sanitization / rate limiting ----

    @Test
    fun `sanitizeInput trims whitespace and strips angle brackets`() {
        assertEquals("hello", SecurityUtils.sanitizeInput("  hello  "))
        val sanitized = SecurityUtils.sanitizeInput("<b>")
        assertFalse(sanitized.contains("<"))
        assertFalse(sanitized.contains(">"))
    }

    @Test
    fun `isRateLimited flags too many recent attempts`() {
        // 5 recent attempts -> limited
        assertTrue(
            SecurityUtils.isRateLimited(
                attempts = 5,
                timeWindow = System.currentTimeMillis(),
                maxAttempts = 5,
                windowMinutes = 15
            )
        )
        // fewer attempts -> not limited
        assertFalse(
            SecurityUtils.isRateLimited(
                attempts = 2,
                timeWindow = System.currentTimeMillis(),
                maxAttempts = 5,
                windowMinutes = 15
            )
        )
        // old window -> not limited
        assertFalse(
            SecurityUtils.isRateLimited(
                attempts = 5,
                timeWindow = System.currentTimeMillis() - 20 * 60 * 1000L,
                maxAttempts = 5,
                windowMinutes = 15
            )
        )
    }
}
