package com.avinashpatil.monzobank.exception

import com.avinashpatil.monzobank.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    // Custom Business Exceptions
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("User not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "User not found", null))
    }
    
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("User already exists: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.message ?: "User already exists", null))
    }
    
    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(ex: AccountNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Account not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Account not found", null))
    }
    
    @ExceptionHandler(InsufficientFundsException::class)
    fun handleInsufficientFundsException(ex: InsufficientFundsException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Insufficient funds: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Insufficient funds", null))
    }
    
    @ExceptionHandler(InvalidTransactionException::class)
    fun handleInvalidTransactionException(ex: InvalidTransactionException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Invalid transaction: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Invalid transaction", null))
    }
    
    @ExceptionHandler(CardNotFoundException::class)
    fun handleCardNotFoundException(ex: CardNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Card not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Card not found", null))
    }
    
    @ExceptionHandler(PotNotFoundException::class)
    fun handlePotNotFoundException(ex: PotNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Pot not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Pot not found", null))
    }
    
    @ExceptionHandler(InvestmentNotFoundException::class)
    fun handleInvestmentNotFoundException(ex: InvestmentNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Investment not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Investment not found", null))
    }
    
    @ExceptionHandler(LoanNotFoundException::class)
    fun handleLoanNotFoundException(ex: LoanNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Loan not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message ?: "Loan not found", null))
    }
    
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(ex: InvalidCredentialsException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Invalid credentials: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(ex.message ?: "Invalid credentials", null))
    }
    
    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Token expired: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(ex.message ?: "Token expired", null))
    }
    
    @ExceptionHandler(BusinessRuleException::class)
    fun handleBusinessRuleException(ex: BusinessRuleException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Business rule violation: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Business rule violation", null))
    }
    
    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Validation error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Validation error", null))
    }
    
    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceededException(ex: RateLimitExceededException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Rate limit exceeded: {}", ex.message)
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ApiResponse.error(ex.message ?: "Rate limit exceeded", null))
    }
    
    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalServiceException(ex: ExternalServiceException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("External service error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(ApiResponse.error(ex.message ?: "External service error", null))
    }
    
    @ExceptionHandler(PaymentProcessingException::class)
    fun handlePaymentProcessingException(ex: PaymentProcessingException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Payment processing error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
            .body(ApiResponse.error(ex.message ?: "Payment processing error", null))
    }
    
    @ExceptionHandler(FraudDetectedException::class)
    fun handleFraudDetectedException(ex: FraudDetectedException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Fraud detected: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.message ?: "Fraud detected", null))
    }
    
    @ExceptionHandler(KycVerificationException::class)
    fun handleKycVerificationException(ex: KycVerificationException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("KYC verification error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.message ?: "KYC verification required", null))
    }
    
    @ExceptionHandler(DatabaseException::class)
    fun handleDatabaseException(ex: DatabaseException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Database error: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Database error occurred", null))
    }
    
    @ExceptionHandler(SystemException::class)
    fun handleSystemException(ex: SystemException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("System error: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("System error occurred", null))
    }
    
    // Spring Security Exceptions
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Access denied: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied: Insufficient permissions", null))
    }
    
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Authentication error: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Authentication failed", null))
    }
    
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Bad credentials: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid username or password", null))
    }
    
    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ApiResponse<Map<String, String>>> {
        logger.error("Validation error: {}", ex.message)
        
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[fieldName] = errorMessage
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation failed", errors))
    }
    
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Message not readable: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Invalid request format", null))
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Type mismatch: {}", ex.message)
        val message = "Invalid value for parameter '${ex.name}': ${ex.value}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(message, null))
    }
    
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(ex: MissingServletRequestParameterException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Missing parameter: {}", ex.message)
        val message = "Missing required parameter: ${ex.parameterName}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(message, null))
    }
    
    // HTTP Method Exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Method not supported: {}", ex.message)
        val message = "HTTP method '${ex.method}' is not supported for this endpoint"
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.error(message, null))
    }
    
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("No handler found: {}", ex.message)
        val message = "Endpoint not found: ${ex.httpMethod} ${ex.requestURL}"
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(message, null))
    }
    
    // Generic Exception Handler
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error: {}", ex.message, ex)
        
        // Don't expose internal error details in production
        val message = if (isProductionEnvironment()) {
            "An unexpected error occurred. Please try again later."
        } else {
            ex.message ?: "An unexpected error occurred"
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(message, null))
    }
    
    // Runtime Exception Handler
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Runtime error: {}", ex.message, ex)
        
        val message = if (isProductionEnvironment()) {
            "A runtime error occurred. Please try again later."
        } else {
            ex.message ?: "A runtime error occurred"
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(message, null))
    }
    
    // Illegal Argument Exception Handler
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Illegal argument: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Invalid argument provided", null))
    }
    
    // Illegal State Exception Handler
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Illegal state: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.message ?: "Invalid operation state", null))
    }
    
    private fun isProductionEnvironment(): Boolean {
        // In a real application, this would check the active profile
        val activeProfiles = System.getProperty("spring.profiles.active") ?: ""
        return activeProfiles.contains("prod") || activeProfiles.contains("production")
    }
    
    private fun createErrorDetails(request: WebRequest, status: HttpStatus, message: String): Map<String, Any> {
        return mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to message,
            "path" to request.getDescription(false).removePrefix("uri=")
        )
    }
}