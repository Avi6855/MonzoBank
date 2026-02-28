package com.avinashpatil.monzobank.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExistsException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class AccountAlreadyExistsException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class TransactionNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InsufficientFundsException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidTransactionException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class CardNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class CardBlockedException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class CardExpiredException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class DailyLimitExceededException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class MonthlyLimitExceededException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class PotNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class InvestmentNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvestmentTradingException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class LoanNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class LoanApplicationException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentialsException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenExpiredException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidTokenException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.FORBIDDEN)
class AccessDeniedException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BusinessRuleException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class RateLimitExceededException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ExternalServiceException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PaymentProcessingException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class FraudDetectedException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class KycVerificationException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class DatabaseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class SystemException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)