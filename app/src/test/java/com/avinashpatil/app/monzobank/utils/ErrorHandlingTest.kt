package com.avinashpatil.app.monzobank.utils

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Local unit tests for [AppError], [Result], [Throwable.toAppError] and [ErrorHandler] —
 * the error model every repository and ViewModel in the app maps failures through.
 *
 * Only JVM-safe members are covered here. Compose-only members such as [ErrorMessage]
 * and [LoadingIndicator] belong in an instrumented / Robolectric test.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class ErrorHandlingTest {

    // ---- Exception mapping ----

    @Test
    fun `network failures map to NetworkError`() {
        assertTrue(ConnectException("connection refused").toAppError() is AppError.NetworkError)
        assertTrue(UnknownHostException("no dns").toAppError() is AppError.NetworkError)

        val timeout = SocketTimeoutException("too slow").toAppError() as AppError.NetworkError
        assertEquals("Connection timeout", timeout.message)
        assertEquals(
            "The request timed out. Please check your connection and try again",
            timeout.userMessage
        )
    }

    @Test
    fun `security and argument failures map to the matching AppError`() {
        val security = SecurityException("biometric failed").toAppError()
        assertTrue(security is AppError.SecurityError)
        assertEquals("biometric failed", security.message)

        val argument = IllegalArgumentException("bad input").toAppError()
        assertTrue(argument is AppError.ValidationError)
        assertEquals("bad input", argument.message)

        assertTrue(IllegalStateException("wrong state").toAppError() is AppError.ValidationError)
    }

    @Test
    fun `unexpected failures fall back to UnknownError`() {
        val boom = RuntimeException("boom").toAppError()
        assertTrue(boom is AppError.UnknownError)
        assertEquals("boom", boom.message)
        assertEquals("An unexpected error occurred. Please try again", boom.userMessage)

        // A throwable without a message still produces a readable error
        assertEquals("Unknown error", Throwable().toAppError().message)
    }

    @Test
    fun `AppError subtypes carry user facing copy`() {
        assertEquals("Please check your internet connection and try again", AppError.NetworkError().userMessage)
        assertEquals("Please log in again to continue", AppError.AuthenticationError().userMessage)
        assertEquals("You don't have enough funds for this transaction", AppError.InsufficientFundsError().userMessage)
        assertEquals("Too many requests. Please wait a moment and try again", AppError.RateLimitError().userMessage)
        assertEquals("This transaction has been flagged for security review", AppError.SecurityError().userMessage)

        // Validation errors reuse the message itself as the user facing text
        assertEquals("Name is required", AppError.ValidationError("Name is required").userMessage)
    }

    // ---- Result wrapper ----

    @Test
    fun `Result wraps success data`() {
        val success = Result.Success(42)
        assertEquals(42, success.data)
    }

    @Test
    fun `Result keeps error and loading states apart`() {
        val failure: Result<Int> = Result.Error(AppError.ValidationError("Amount is required"))
        val loading: Result<Int> = Result.Loading

        assertTrue(failure is Result.Error)
        assertEquals("Amount is required", (failure as Result.Error).error.userMessage)
        assertTrue(loading is Result.Loading)
    }

    // ---- ErrorHandler ----

    @Test
    fun `safeApiCall returns the value of a healthy call`() = runBlocking {
        val result = ErrorHandler.safeApiCall { "ok" }
        assertTrue(result is Result.Success)
        assertEquals("ok", (result as Result.Success).data)
    }

    @Test
    fun `safeApiCall converts a network failure into a Result Error`() = runBlocking {
        val result = ErrorHandler.safeApiCall<String> { throw ConnectException("offline") }
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is AppError.NetworkError)
    }

    @Test
    fun `safeDatabaseCall reports failures as an UnknownError`() = runBlocking {
        val result = ErrorHandler.safeDatabaseCall<String> { throw IllegalStateException("db down") }
        val error = (result as Result.Error).error
        assertTrue(error.message.startsWith("Database error:"))
        assertEquals("Failed to save data. Please try again", error.userMessage)
    }

    @Test
    fun `validateTransactionAmount rejects zero, garbage and over-limit amounts`() {
        val balance = BigDecimal("100")

        val zero = ErrorHandler.validateTransactionAmount("0", balance)
        assertTrue(zero is AppError.ValidationError)
        assertEquals("Amount must be greater than zero", zero!!.message)

        val garbage = ErrorHandler.validateTransactionAmount("abc", balance)
        assertTrue(garbage is AppError.ValidationError)
        assertEquals("Please enter a valid amount", garbage!!.message)

        val overLimit = ErrorHandler.validateTransactionAmount("20000", BigDecimal("50000"))
        assertTrue(overLimit is AppError.ValidationError)
        assertEquals("Amount cannot exceed £10,000", overLimit!!.message)
    }

    @Test
    fun `validateTransactionAmount accepts amounts covered by balance or overdraft`() {
        assertNull(ErrorHandler.validateTransactionAmount("100", BigDecimal("100")))
        assertNull(ErrorHandler.validateTransactionAmount("140", BigDecimal("100"), BigDecimal("50")))
    }

    @Test
    fun `validateTransactionAmount reports insufficient funds`() {
        val error = ErrorHandler.validateTransactionAmount("200", BigDecimal("100"))
        assertTrue(error is AppError.InsufficientFundsError)
    }

    @Test
    fun `checkRateLimit allows the first requests and then blocks`() {
        val userId = "unit-test-rate-limit-user"
        assertNull(ErrorHandler.checkRateLimit(userId, maxRequests = 2))
        assertNull(ErrorHandler.checkRateLimit(userId, maxRequests = 2))
        assertTrue(ErrorHandler.checkRateLimit(userId, maxRequests = 2) is AppError.RateLimitError)
    }

    @Test
    fun `checkRateLimit keeps counters per user`() {
        assertNull(ErrorHandler.checkRateLimit("unit-test-user-a", maxRequests = 1))
        assertNull(ErrorHandler.checkRateLimit("unit-test-user-b", maxRequests = 1))
        assertTrue(ErrorHandler.checkRateLimit("unit-test-user-a", maxRequests = 1) is AppError.RateLimitError)
    }

    @Test
    fun `checkRateLimit restarts the counter once the window has passed`() {
        // A negative window means the previous window is already over, so the counter resets
        val userId = "unit-test-window-user"
        assertNull(ErrorHandler.checkRateLimit(userId, maxRequests = 1, windowMs = -1))
        assertNull(ErrorHandler.checkRateLimit(userId, maxRequests = 1, windowMs = -1))
    }

    // ---- Small helpers used across the app ----

    @Test
    fun `toBigDecimalOrNull parses valid amounts and rejects garbage`() {
        assertEquals(BigDecimal("12.50"), "12.50".toBigDecimalOrNull())
        assertNull("abc".toBigDecimalOrNull())
        assertNull("".toBigDecimalOrNull())
    }

    @Test
    fun `toIntOrNull parses whole numbers only`() {
        assertEquals(42, "42".toIntOrNull())
        assertNull("4.2".toIntOrNull())
        assertNull("nope".toIntOrNull())
    }

    @Test
    fun `NetworkUtils reports a usable connection`() {
        assertTrue(NetworkUtils.isNetworkAvailable())
        assertNull(NetworkUtils.getNetworkError())
    }

    @Test
    fun `ErrorLogger logs without throwing`() {
        ErrorLogger.logError(AppError.NetworkError(), context = "unit-test")
    }
}
