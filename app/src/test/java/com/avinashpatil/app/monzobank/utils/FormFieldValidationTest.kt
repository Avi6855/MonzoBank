package com.avinashpatil.app.monzobank.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests for the account / card / personal-detail validators in
 * [com.avinashpatil.app.monzobank.utils] that the existing suites did not touch yet:
 * [validatePassword], [validatePhoneNumber], [validateAccountNumber], [validateSortCode],
 * [validateCardNumber], [validateReference], [validateAddress], [validatePostcode] and
 * [validateDateOfBirth].
 *
 * Note: [validateEmail] is intentionally NOT covered — it delegates to
 * `android.util.Patterns.EMAIL_ADDRESS`, which is a stub on the local JVM runtime.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class FormFieldValidationTest {

    // ---- Password ----

    @Test
    fun `validatePassword requires a value`() {
        assertEquals("Password is required", validatePassword(""))
        assertEquals("Password is required", validatePassword("   "))
    }

    @Test
    fun `validatePassword enforces the 8 character minimum`() {
        assertEquals("Password must be at least 8 characters", validatePassword("Pa@1"))
    }

    @Test
    fun `validatePassword needs upper, lower, digit and special characters`() {
        val expected = "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
        assertEquals(expected, validatePassword("password"))
        assertEquals(expected, validatePassword("PASSWORD1"))
        assertEquals(expected, validatePassword("Password1"))
    }

    @Test
    fun `validatePassword accepts a strong password`() {
        assertNull(validatePassword("Pass@123"))
        assertNull(validatePassword("Str0ng#Pass!"))
    }

    // ---- Phone number ----

    @Test
    fun `validatePhoneNumber requires a value`() {
        assertEquals("Phone number is required", validatePhoneNumber(""))
    }

    @Test
    fun `validatePhoneNumber accepts UK mobile formats`() {
        assertNull(validatePhoneNumber("+44 7123456789"))
        assertNull(validatePhoneNumber("07123456789"))
    }

    @Test
    fun `validatePhoneNumber rejects malformed numbers`() {
        assertEquals("Please enter a valid UK phone number", validatePhoneNumber("12345"))
        assertEquals("Please enter a valid UK phone number", validatePhoneNumber("071234567"))
    }

    // ---- Account number / sort code ----

    @Test
    fun `validateAccountNumber insists on 8 digits`() {
        assertEquals("Account number is required", validateAccountNumber(""))
        assertEquals("Account number must be 8 digits", validateAccountNumber("1234567"))
        assertEquals("Account number must be 8 digits", validateAccountNumber("123456789"))
        assertEquals("Account number must be 8 digits", validateAccountNumber("1234567a"))
        assertNull(validateAccountNumber("12345678"))
    }

    @Test
    fun `validateSortCode insists on the XX-XX-XX format`() {
        assertEquals("Sort code is required", validateSortCode(""))
        assertEquals("Sort code must be in format XX-XX-XX", validateSortCode("123456"))
        assertEquals("Sort code must be in format XX-XX-XX", validateSortCode("12-34-5"))
        assertNull(validateSortCode("12-34-56"))
    }

    // ---- Card number ----

    @Test
    fun `validateCardNumber requires a value`() {
        assertEquals("Card number is required", validateCardNumber(""))
    }

    @Test
    fun `validateCardNumber accepts a card that passes the Luhn check`() {
        assertNull(validateCardNumber("4242424242424242"))
        assertNull(validateCardNumber("5555555555554444"))
    }

    @Test
    fun `validateCardNumber rejects wrong length and failed Luhn checks`() {
        assertEquals("Please enter a valid card number", validateCardNumber("1234"))
        assertEquals("Please enter a valid card number", validateCardNumber("4242424242424241"))
        assertEquals("Please enter a valid card number", validateCardNumber("not-a-card"))
    }

    // ---- Payment reference ----

    @Test
    fun `validateReference caps the length at 18 characters`() {
        assertEquals("Reference cannot exceed 18 characters", validateReference("R".repeat(19)))
        assertNull(validateReference("R".repeat(18)))
    }

    @Test
    fun `validateReference rejects markup characters`() {
        assertEquals("Reference contains invalid characters", validateReference("Rent <March>"))
        assertEquals("Reference contains invalid characters", validateReference("Rent & March"))
        assertEquals("Reference contains invalid characters", validateReference("Rent 'March'"))
        assertNull(validateReference("Rent March 2026"))
    }

    // ---- Address ----

    @Test
    fun `validateAddress requires a complete address`() {
        assertEquals("Address is required", validateAddress(""))
        assertEquals("Please enter a complete address", validateAddress("Flat 1"))
        assertEquals("Address cannot exceed 100 characters", validateAddress("A".repeat(101)))
        assertNull(validateAddress("Flat 1, 12 High Street, London"))
    }

    // ---- Postcode ----

    @Test
    fun `validatePostcode accepts UK postcodes in any case`() {
        assertNull(validatePostcode("SW1A 1AA"))
        assertNull(validatePostcode("sw1a 1aa"))
        assertNull(validatePostcode("M1 1AE"))
    }

    @Test
    fun `validatePostcode rejects non-UK postcodes`() {
        assertEquals("Postcode is required", validatePostcode(""))
        assertEquals("Please enter a valid UK postcode", validatePostcode("ABC"))
        assertEquals("Please enter a valid UK postcode", validatePostcode("12345 67890"))
    }

    // ---- Date of birth ----

    @Test
    fun `validateDateOfBirth requires DD slash MM slash YYYY`() {
        assertEquals("Date of birth is required", validateDateOfBirth(""))
        assertEquals("Please use DD/MM/YYYY format", validateDateOfBirth("1-1-1990"))
        assertEquals("Please enter a valid date in DD/MM/YYYY format", validateDateOfBirth("aa/bb/cccc"))
    }

    @Test
    fun `validateDateOfBirth validates day, month and year ranges`() {
        assertEquals("Invalid day", validateDateOfBirth("32/01/1990"))
        assertEquals("Invalid day", validateDateOfBirth("00/01/1990"))
        assertEquals("Invalid month", validateDateOfBirth("01/13/1990"))
        assertEquals("Invalid year", validateDateOfBirth("01/01/1800"))
        assertEquals("Invalid year", validateDateOfBirth("01/01/2015"))
        assertNull(validateDateOfBirth("15/08/1990"))
    }

    @Test
    fun `validateDateOfBirth blocks applicants under 18`() {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val underageYear = currentYear - 17
        // The validator only accepts years up to 2010, so the age rule is only reachable until 2027
        if (underageYear <= 2010) {
            assertEquals("You must be at least 18 years old", validateDateOfBirth("01/01/$underageYear"))
        }
    }
}
