package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.Contact

/**
 * Repository interface for contact management
 */
interface ContactRepository {
    
    /**
     * Get all contacts for a user
     */
    suspend fun getContacts(userId: String): Result<List<Contact>>
    
    /**
     * Add a new contact
     */
    suspend fun addContact(userId: String, contact: Contact): Result<Unit>
    
    /**
     * Update an existing contact
     */
    suspend fun updateContact(userId: String, contact: Contact): Result<Unit>
    
    /**
     * Delete a contact
     */
    suspend fun deleteContact(userId: String, contactId: String): Result<Unit>
    
    /**
     * Search contacts by name or account number
     */
    suspend fun searchContacts(
        userId: String,
        query: String
    ): Result<List<Contact>>
    
    /**
     * Get favorite contacts
     */
    suspend fun getFavoriteContacts(userId: String): Result<List<Contact>>
    
    /**
     * Mark contact as favorite
     */
    suspend fun markAsFavorite(
        userId: String,
        contactId: String,
        isFavorite: Boolean
    ): Result<Unit>
    
    /**
     * Get recent contacts
     */
    suspend fun getRecentContacts(
        userId: String,
        limit: Int = 10
    ): Result<List<Contact>>
}

/**
 * Contact data model
 */
data class Contact(
    val id: String,
    val userId: String,
    val name: String,
    val accountNumber: String? = null,
    val routingNumber: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val bankName: String? = null,
    val nickname: String? = null,
    val isFavorite: Boolean = false,
    val lastUsed: java.util.Date? = null,
    val createdAt: java.util.Date,
    val updatedAt: java.util.Date
)