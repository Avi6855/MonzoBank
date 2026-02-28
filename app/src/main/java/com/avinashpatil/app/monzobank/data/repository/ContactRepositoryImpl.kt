package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.model.Contact
import com.avinashpatil.app.monzobank.domain.repository.ContactRepository
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    // TODO: Add ContactDao, etc.
) : ContactRepository {
    
    private val contacts = mutableMapOf<String, MutableList<Contact>>()
    
    override suspend fun getContacts(userId: String): Result<List<Contact>> {
        return try {
            val userContacts = contacts[userId] ?: emptyList()
            Result.success(userContacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addContact(userId: String, contact: Contact): Result<Unit> {
        return try {
            val userContacts = contacts.getOrPut(userId) { mutableListOf() }
            val newContact = contact.copy(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            userContacts.add(newContact)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateContact(userId: String, contact: Contact): Result<Unit> {
        return try {
            val userContacts = contacts[userId] ?: return Result.failure(Exception("User not found"))
            val index = userContacts.indexOfFirst { it.id == contact.id }
            if (index != -1) {
                userContacts[index] = contact.copy(updatedAt = LocalDateTime.now())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Contact not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteContact(userId: String, contactId: String): Result<Unit> {
        return try {
            val userContacts = contacts[userId] ?: return Result.failure(Exception("User not found"))
            val removed = userContacts.removeIf { it.id == contactId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Contact not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchContacts(
        userId: String,
        query: String
    ): Result<List<Contact>> {
        return try {
            val userContacts = contacts[userId] ?: emptyList()
            val filteredContacts = userContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.accountNumber?.contains(query, ignoreCase = true) == true ||
                it.email?.contains(query, ignoreCase = true) == true
            }
            Result.success(filteredContacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFavoriteContacts(userId: String): Result<List<Contact>> {
        return try {
            val userContacts = contacts[userId] ?: emptyList()
            val favoriteContacts = userContacts.filter { it.isFavorite }
            Result.success(favoriteContacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAsFavorite(
        userId: String,
        contactId: String,
        isFavorite: Boolean
    ): Result<Unit> {
        return try {
            val userContacts = contacts[userId] ?: return Result.failure(Exception("User not found"))
            val contact = userContacts.find { it.id == contactId }
            if (contact != null) {
                val index = userContacts.indexOf(contact)
                userContacts[index] = contact.copy(
                    isFavorite = isFavorite,
                    updatedAt = LocalDateTime.now()
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Contact not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecentContacts(
        userId: String,
        limit: Int
    ): Result<List<Contact>> {
        return try {
            val userContacts = contacts[userId] ?: emptyList()
            val recentContacts = userContacts
                .filter { it.lastUsed != null }
                .sortedByDescending { it.lastUsed }
                .take(limit)
            Result.success(recentContacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}