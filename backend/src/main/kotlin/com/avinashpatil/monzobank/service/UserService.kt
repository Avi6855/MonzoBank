package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.CreateUserRequest
import com.avinashpatil.monzobank.dto.UpdateUserRequest
import com.avinashpatil.monzobank.dto.UserResponse
import com.avinashpatil.monzobank.entity.User
import com.avinashpatil.monzobank.entity.KycStatus
import com.avinashpatil.monzobank.exception.UserNotFoundException
import com.avinashpatil.monzobank.exception.UserAlreadyExistsException
import com.avinashpatil.monzobank.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun createUser(request: CreateUserRequest): UserResponse {
        // Check if user already exists
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists")
        }
        
        if (userRepository.existsByPhone(request.phone)) {
            throw UserAlreadyExistsException("User with phone ${request.phone} already exists")
        }
        
        // Create new user
        val user = User(
            email = request.email,
            phone = request.phone,
            passwordHash = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            dateOfBirth = request.dateOfBirth,
            address = request.address
        )
        
        val savedUser = userRepository.save(user)
        return mapToUserResponse(savedUser)
    }
    
    @Transactional(readOnly = true)
    fun getUserById(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        return mapToUserResponse(user)
    }
    
    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserResponse {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("User not found with email: $email")
        return mapToUserResponse(user)
    }
    
    @Transactional(readOnly = true)
    fun getUserByPhone(phone: String): UserResponse {
        val user = userRepository.findByPhone(phone)
            ?: throw UserNotFoundException("User not found with phone: $phone")
        return mapToUserResponse(user)
    }
    
    fun updateUser(userId: UUID, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(
            firstName = request.firstName ?: user.firstName,
            lastName = request.lastName ?: user.lastName,
            phone = request.phone ?: user.phone,
            address = request.address ?: user.address,
            profileImageUrl = request.profileImageUrl ?: user.profileImageUrl
        )
        
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun updateKycStatus(userId: UUID, kycStatus: KycStatus): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(kycStatus = kycStatus)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun verifyEmail(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(emailVerified = true)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun verifyPhone(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(phoneVerified = true)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun enableBiometric(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(biometricEnabled = true)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun disableBiometric(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(biometricEnabled = false)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    fun deactivateUser(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found with id: $userId") }
        
        val updatedUser = user.copy(isActive = false)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }
    
    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { mapToUserResponse(it) }
    }
    
    @Transactional(readOnly = true)
    fun searchUsersByName(name: String): List<UserResponse> {
        return userRepository.findByNameContaining(name).map { mapToUserResponse(it) }
    }
    
    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            phone = user.phone,
            firstName = user.firstName,
            lastName = user.lastName,
            dateOfBirth = user.dateOfBirth,
            address = user.address,
            kycStatus = user.kycStatus,
            biometricEnabled = user.biometricEnabled,
            isActive = user.isActive,
            emailVerified = user.emailVerified,
            phoneVerified = user.phoneVerified,
            profileImageUrl = user.profileImageUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}