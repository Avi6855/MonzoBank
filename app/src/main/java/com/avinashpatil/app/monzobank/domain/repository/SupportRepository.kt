package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class SupportTicket(
    val id: String,
    val userId: String,
    val subject: String,
    val description: String,
    val category: SupportCategory,
    val priority: SupportPriority,
    val status: SupportStatus,
    val assignedTo: String? = null,
    val attachments: List<String> = emptyList(),
    val messages: List<SupportMessage> = emptyList(),
    val resolution: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val resolvedAt: LocalDateTime? = null
)

data class SupportMessage(
    val id: String,
    val ticketId: String,
    val senderId: String,
    val senderType: SenderType,
    val message: String,
    val attachments: List<String> = emptyList(),
    val createdAt: LocalDateTime
)

data class SupportAgent(
    val id: String,
    val name: String,
    val email: String,
    val department: String,
    val specialties: List<SupportCategory>,
    val isAvailable: Boolean,
    val rating: Double
)

enum class SupportCategory {
    ACCOUNT_ISSUES,
    TRANSACTION_PROBLEMS,
    CARD_ISSUES,
    TECHNICAL_SUPPORT,
    BILLING_QUESTIONS,
    SECURITY_CONCERNS,
    GENERAL_INQUIRY,
    COMPLAINT
}

enum class SupportPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class SupportStatus {
    OPEN,
    IN_PROGRESS,
    WAITING_FOR_CUSTOMER,
    RESOLVED,
    CLOSED,
    ESCALATED
}

enum class SenderType {
    CUSTOMER,
    AGENT,
    SYSTEM
}

interface SupportRepository {
    suspend fun createTicket(ticket: SupportTicket): Result<String>
    suspend fun getTickets(userId: String): Result<List<SupportTicket>>
    suspend fun getTicket(ticketId: String): Result<SupportTicket?>
    suspend fun updateTicketStatus(ticketId: String, status: SupportStatus): Result<Unit>
    suspend fun assignTicket(ticketId: String, agentId: String): Result<Unit>
    suspend fun addMessage(message: SupportMessage): Result<Unit>
    suspend fun getMessages(ticketId: String): Result<List<SupportMessage>>
    suspend fun resolveTicket(ticketId: String, resolution: String): Result<Unit>
    suspend fun getAvailableAgents(category: SupportCategory): Result<List<SupportAgent>>
    suspend fun escalateTicket(ticketId: String, reason: String): Result<Unit>
    suspend fun getSupportAnalytics(): Result<Map<String, Any>>
}