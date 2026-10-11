package com.avinashpatil.app.monzobank.utils

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

/**
 * Local unit tests for fintech form validation.
 *
 * Covers pure-JVM validators used across auth / payments / transfer flows:
 * [validateAmount], [validateTransferAmount], [validateConfirmPassword],
 * [validateName], [validatePin], [validateRequired], [validateMinLength],
 * [validateMaxLength], [combineValidators] and [FormValidationState].
 *
 * Note: [validateEmail] is intentionally NOT covered here — it delegates to
 * `android.util.Patterns.EMAIL_ADDRESS`, which is unavailable on the local JVM
 * unit-test runtime. It belongs in an instrumented / Robolectric test.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class InputValidationTest {

    // ---- Amount ----

    @Test
    fun `validateAmount returns error for blank input`() {
        assertEquals("Amount is required", validateAmount(""))
        assertEquals("Amount is required", validateAmount("   "))
    }

    @Test
    fun `validateAmount returns error for non-numeric input`() {
        assertEquals("Please enter a valid amount", validateAmount("abc"))
    }

    @Test
    fun `validateAmount rejects zero and negative values`() {
        assertEquals("Amount must be greater than zero", validateAmount("0"))
        assertEquals("Amount must be greater than zero", validateAmount("-5"))
    }

    @Test
    fun `validateAmount rejects more than 2 decimal places`() {
        assertEquals("Please enter a valid amount", validateAmount("10.123"))
    }

    @Test
    fun `validateAmount enforces 10000 upper limit`() {
        assertEquals("Amount cannot exceed £10,000", validateAmount("10000.01"))
    }

    @Test
    fun `validateAmount accepts valid amounts`() {
        assertNull(validateAmount("10"))
        assertNull(validateAmount("10.50"))
        assertNull(validateAmount("10000"))
    }

    // ---- Transfer amount (balance + limits) ----

    @Test
    fun `validateTransferAmount accepts amount within balance`() {
        assertNull(
            validateTransferAmount(
                amount = "50",
                accountBalance = BigDecimal("100")
            )
        )
    }

    @Test
    fun `validateTransferAmount rejects amount above balance`() {
        assertEquals(
            "Insufficient funds",
            validateTransferAmount(
                amount = "200",
                accountBalance = BigDecimal("100")
            )
        )
    }

    @Test
    fun `validateTransferAmount respects overdraft limit`() {
        // 100 balance + 50 overdraft covers 140
        assertNull(
            validateTransferAmount(
                amount = "140",
                accountBalance = BigDecimal("100"),
                overdraftLimit = BigDecimal("50")
            )
        )
    }

    @Test
    fun `validateTransferAmount enforces daily limit`() {
        val error = validateTransferAmount(
            amount = "500",
            accountBalance = BigDecimal("1000"),
            dailyLimit = BigDecimal("100")
        )
        assertNotNull(error)
        assertTrue(error!!.startsWith("Amount exceeds daily limit"))
    }

    // ---- Confirm password ----

    @Test
    fun `validateConfirmPassword detects blank and mismatch`() {
        assertEquals(
            "Please confirm your password",
            validateConfirmPassword("Pass@1234", "")
        )
        assertEquals(
            "Passwords do not match",
            validateConfirmPassword("Pass@1234", "Pass@9999")
        )
    }

    @Test
    fun `validateConfirmPassword accepts matching passwords`() {
        assertNull(validateConfirmPassword("Pass@1234", "Pass@1234"))
    }

    // ---- Name ----

    @Test
    fun `validateName rejects blank and too-short names`() {
        assertEquals("Name is required", validateName(""))
        assertEquals("Name must be at least 2 characters", validateName("A"))
    }

    @Test
    fun `validateName rejects names with digits`() {
        assertNotNull(validateName("Avinash123"))
    }

    @Test
    fun `validateName accepts valid names`() {
        assertNull(validateName("Avinash Patil"))
        assertNull(validateName("O'Brien-Smith"))
    }

    // ---- PIN (regex ^\d{4,6}$) ----

    @Test
    fun `validatePin accepts 4 to 6 digit pins`() {
        assertNull(validatePin("1234"))
        assertNull(validatePin("123456"))
    }

    @Test
    fun `validatePin rejects blank and malformed pins`() {
        assertEquals("PIN is required", validatePin(""))
        assertEquals("PIN must be 4-6 digits", validatePin("123"))
        assertEquals("PIN must be 4-6 digits", validatePin("1234567"))
        assertEquals("PIN must be 4-6 digits", validatePin("12a4"))
    }

    // ---- Generic validators ----

    @Test
    fun `validateRequired flags blank values`() {
        assertEquals("Email is required", validateRequired("", "Email"))
        assertNull(validateRequired("x", "Email"))
    }

    @Test
    fun `validateMinLength and validateMaxLength enforce bounds`() {
        assertEquals(
            "Password must be at least 8 characters",
            validateMinLength("short", 8, "Password")
        )
        assertNull(validateMinLength("longenough", 8, "Password"))
        assertEquals(
            "Name cannot exceed 5 characters",
            validateMaxLength("toolongname", 5, "Name")
        )
        assertNull(validateMaxLength("ok", 5, "Name"))
    }

    @Test
    fun `combineValidators returns first error only`() {
        val combined = combineValidators(
            { validateRequired(it, "Field") },
            { validateMinLength(it, 5, "Field") }
        )
        assertEquals("Field is required", combined(""))
        assertEquals("Field must be at least 5 characters", combined("abc"))
        assertNull(combined("abcdef"))
    }

    // ---- FormValidationState ----

    @Test
    fun `FormValidationState tracks and clears errors`() {
        val state = FormValidationState()

        assertFalse(state.hasErrors())

        state.setError("email", "Email is required")
        assertTrue(state.hasErrors())
        assertEquals("Email is required", state.getError("email"))
        assertFalse(state.isFieldValid("email"))
        assertTrue(state.isFieldValid("password"))

        state.clearError("email")
        assertFalse(state.hasErrors())
        assertNull(state.getError("email"))
    }
}
