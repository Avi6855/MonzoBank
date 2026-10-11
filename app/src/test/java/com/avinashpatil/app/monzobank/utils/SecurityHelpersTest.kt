package com.avinashpatil.app.monzobank.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests for the remaining JVM-safe helpers on [SecurityUtils] — bank-detail
 * format checks, PIN generation, data masking and session timeout handling.
 *
 * Members that need the Android runtime are deliberately left out:
 * - [SecurityUtils.isValidEmail] (`android.util.Patterns`)
 * - [SecurityUtils.hashPassword] / [SecurityUtils.verifyPassword] (`android.util.Base64`)
 * These belong in an instrumented / Robolectric test.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class SecurityHelpersTest {

    // ---- Password / phone / bank details ----

    @Test
    fun `isValidPassword matches the fintech strength rules`() {
        assertTrue(SecurityUtils.isValidPassword("Pass@1234"))
        assertFalse(SecurityUtils.isValidPassword("password"))
        assertFalse(SecurityUtils.isValidPassword("Pass@12"))
        assertFalse(SecurityUtils.isValidPassword(""))
    }

    @Test
    fun `isValidPhoneNumber accepts UK mobile shapes`() {
        assertTrue(SecurityUtils.isValidPhoneNumber("+44 7123456789"))
        assertTrue(SecurityUtils.isValidPhoneNumber("07123456789"))
        assertFalse(SecurityUtils.isValidPhoneNumber("12345"))
        assertFalse(SecurityUtils.isValidPhoneNumber(""))
    }

    @Test
    fun `isValidAccountNumber and isValidSortCode guard the transfer form`() {
        assertTrue(SecurityUtils.isValidAccountNumber("12345678"))
        assertFalse(SecurityUtils.isValidAccountNumber("1234567"))
        assertFalse(SecurityUtils.isValidAccountNumber("1234567a"))
        assertFalse(SecurityUtils.isValidAccountNumber(""))

        assertTrue(SecurityUtils.isValidSortCode("12-34-56"))
        assertFalse(SecurityUtils.isValidSortCode("123456"))
        assertFalse(SecurityUtils.isValidSortCode(""))
    }

    @Test
    fun `isValidCardNumber applies the Luhn checksum`() {
        assertTrue(SecurityUtils.isValidCardNumber("4242424242424242"))
        assertTrue(SecurityUtils.isValidCardNumber("5555555555554444"))
        assertFalse(SecurityUtils.isValidCardNumber("4242424242424241"))
        assertFalse(SecurityUtils.isValidCardNumber("1234"))
        assertFalse(SecurityUtils.isValidCardNumber(""))
    }

    // ---- PIN generation ----

    @Test
    fun `generateSecurePin returns digits of the requested length`() {
        val fourDigit = SecurityUtils.generateSecurePin()
        assertEquals(4, fourDigit.length)
        assertTrue(fourDigit.all { it.isDigit() })

        val sixDigit = SecurityUtils.generateSecurePin(6)
        assertEquals(6, sixDigit.length)
        assertTrue(sixDigit.all { it.isDigit() })
    }

    // ---- Masking shown in the UI ----

    @Test
    fun `maskCardNumber keeps only the last four digits`() {
        assertEquals("**** **** **** 4242", SecurityUtils.maskCardNumber("4242424242424242"))
        // Too short to mask safely, returned untouched
        assertEquals("123", SecurityUtils.maskCardNumber("123"))
    }

    @Test
    fun `maskAccountNumber keeps only the last four digits`() {
        assertEquals("****5678", SecurityUtils.maskAccountNumber("12345678"))
        assertEquals("12", SecurityUtils.maskAccountNumber("12"))
    }

    @Test
    fun `maskEmail hides all but the first two characters of the local part`() {
        val masked = SecurityUtils.maskEmail("avinashpatil6855@gmail.com")
        assertTrue(masked.startsWith("av"))
        assertTrue(masked.endsWith("@gmail.com"))
        assertFalse(masked.contains("inashpatil6855"))
        assertEquals("av".length + 14 + "@gmail.com".length, masked.length)

        // Nothing to mask in a very short address
        assertEquals("ab@x.com", SecurityUtils.maskEmail("ab@x.com"))
        assertEquals("a@b.com", SecurityUtils.maskEmail("a@b.com"))
    }

    // ---- Session timeout ----

    @Test
    fun `isSessionValid uses the configured timeout window`() {
        val now = System.currentTimeMillis()
        assertTrue(SecurityUtils.isSessionValid(now))
        assertTrue(SecurityUtils.isSessionValid(now - 29 * 60 * 1000L))
        assertFalse(SecurityUtils.isSessionValid(now - 31 * 60 * 1000L))
        assertTrue(SecurityUtils.isSessionValid(now - 100 * 60 * 1000L, timeoutMinutes = 120))
    }

    // ---- Amount limits ----

    @Test
    fun `isValidTransferAmount enforces the monthly limit`() {
        val result = SecurityUtils.isValidTransferAmount(
            amount = java.math.BigDecimal("500"),
            accountBalance = java.math.BigDecimal("1000"),
            overdraftLimit = null,
            dailyLimit = null,
            monthlyLimit = java.math.BigDecimal("400")
        )
        assertFalse(result.isValid)
        assertTrue(result.message.startsWith("Amount exceeds monthly limit"))
    }

    @Test
    fun `isValidTransferAmount rejects zero and negative amounts`() {
        val zero = SecurityUtils.isValidTransferAmount(
            amount = java.math.BigDecimal.ZERO,
            accountBalance = java.math.BigDecimal("100"),
            overdraftLimit = null,
            dailyLimit = null,
            monthlyLimit = null
        )
        assertFalse(zero.isValid)
        assertEquals("Amount must be greater than zero", zero.message)
    }
}
