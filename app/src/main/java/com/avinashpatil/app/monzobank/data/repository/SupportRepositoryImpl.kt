package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepositoryImpl @Inject constructor() : SupportRepository {
    
    private val tickets = mutableListOf<SupportTicket>()
    private val messages = mutableListOf<SupportMessage>()
    private val agents = listOf(
        SupportAgent(
            id = "agent1",
            name = "John Smith",
            email = "john.smith@monzobank.com",
            department = "Technical Support",
            specialties = listOf(SupportCategory.TECHNICAL_SUPPORT, SupportCategory.ACCOUNT_ISSUES),
            isAvailable = true,
            rating = 4.8
        ),
        SupportAgent(
            id = "agent2",
            name = "Sarah Johnson",
            email = "sarah.johnson@monzobank.com",
            department = "Customer Service",
            specialties = listOf(SupportCategory.BILLING_QUESTIONS, SupportCategory.GENERAL_INQUIRY),
            isAvailable = true,
            rating = 4.9
        )
    )
    
    override suspend fun createTicket(ticket: SupportTicket): Result<String> {
        return try {
            tickets.add(ticket)
            Result.success(ticket.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTickets(userId: String): Result<List<SupportTicket>> {
        return try {
            val userTickets = tickets.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            Result.success(userTickets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTicket(ticketId: String): Result<SupportTicket?> {
        return try {
            val ticket = tickets.find { it.id == ticketId }
            Result.success(ticket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTicketStatus(ticketId: String, status: SupportStatus): Result<Unit> {
        return try {
            val index = tickets.indexOfFirst { it.id == ticketId }
            if (index != -1) {
                val updated = tickets[index].copy(
                    status = status,
                    updatedAt = LocalDateTime.now(),
                    resolvedAt = if (status == SupportStatus.RESOLVED) LocalDateTime.now() else null
                )
                tickets[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ticket not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun assignTicket(ticketId: String, agentId: String): Result<Unit> {
        return try {
            val index = tickets.indexOfFirst { it.id == ticketId }
            if (index != -1) {
                val updated = tickets[index].copy(
                    assignedTo = agentId,
                    status = SupportStatus.IN_PROGRESS,
                    updatedAt = LocalDateTime.now()
                )
                tickets[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ticket not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addMessage(message: SupportMessage): Result<Unit> {
        return try {
            messages.add(message)
            
            // Update ticket's updated time
            val ticketIndex = tickets.indexOfFirst { it.id == message.ticketId }
            if (ticketIndex != -1) {
                val updated = tickets[ticketIndex].copy(updatedAt = LocalDateTime.now())
                tickets[ticketIndex] = updated
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMessages(ticketId: String): Result<List<SupportMessage>> {
        return try {
            val ticketMessages = messages.filter { it.ticketId == ticketId }
                .sortedBy { it.createdAt }
            Result.success(ticketMessages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resolveTicket(ticketId: String, resolution: String): Result<Unit> {
        return try {
            val index = tickets.indexOfFirst { it.id == ticketId }
            if (index != -1) {
                val updated = tickets[index].copy(
                    status = SupportStatus.RESOLVED,
                    resolution = resolution,
                    resolvedAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                tickets[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ticket not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailableAgents(category: SupportCategory): Result<List<SupportAgent>> {
        return try {
            val availableAgents = agents.filter { agent ->
                agent.isAvailable && agent.specialties.contains(category)
            }.sortedByDescending { it.rating }
            
            Result.success(availableAgents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun escalateTicket(ticketId: String, reason: String): Result<Unit> {
        return try {
            val index = tickets.indexOfFirst { it.id == ticketId }
            if (index != -1) {
                val updated = tickets[index].copy(
                    status = SupportStatus.ESCALATED,
                    priority = SupportPriority.HIGH,
                    updatedAt = LocalDateTime.now()
                )
                tickets[index] = updated
                
                // Add escalation message
                val escalationMessage = SupportMessage(
                    id = UUID.randomUUID().toString(),
                    ticketId = ticketId,
                    senderId = "system",
                    senderType = SenderType.SYSTEM,
                    message = "Ticket escalated: $reason",
                    createdAt = LocalDateTime.now()
                )
                messages.add(escalationMessage)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ticket not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSupportAnalytics(): Result<Map<String, Any>> {
        return try {
            val totalTickets = tickets.size
            val resolvedTickets = tickets.count { it.status == SupportStatus.RESOLVED }
            val averageResolutionTime = "2.5 hours" // Mock data
            val customerSatisfaction = 4.6 // Mock rating
            
            val analytics = mapOf(
                "total_tickets" to totalTickets,
                "resolved_tickets" to resolvedTickets,
                "resolution_rate" to if (totalTickets > 0) (resolvedTickets.toDouble() / totalTickets * 100) else 0.0,
                "average_resolution_time" to averageResolutionTime,
                "customer_satisfaction" to customerSatisfaction,
                "active_agents" to agents.count { it.isAvailable }
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}