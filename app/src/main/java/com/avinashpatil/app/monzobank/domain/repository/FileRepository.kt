package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.FileMetadata
import java.io.File
import java.io.InputStream

/**
 * Repository interface for file operations
 */
interface FileRepository {
    
    /**
     * Upload a file
     */
    suspend fun uploadFile(
        userId: String,
        fileName: String,
        inputStream: InputStream,
        mimeType: String
    ): Result<String> // Returns file URL
    
    /**
     * Download a file
     */
    suspend fun downloadFile(
        userId: String,
        fileUrl: String,
        destinationPath: String
    ): Result<File>
    
    /**
     * Delete a file
     */
    suspend fun deleteFile(
        userId: String,
        fileUrl: String
    ): Result<Unit>
    
    /**
     * Get file metadata
     */
    suspend fun getFileMetadata(
        userId: String,
        fileUrl: String
    ): Result<FileMetadata>
    
    /**
     * List user files
     */
    suspend fun listFiles(
        userId: String,
        folder: String? = null
    ): Result<List<FileMetadata>>
    
    /**
     * Create a folder
     */
    suspend fun createFolder(
        userId: String,
        folderName: String,
        parentFolder: String? = null
    ): Result<Unit>
    
    /**
     * Get file size
     */
    suspend fun getFileSize(
        userId: String,
        fileUrl: String
    ): Result<Long>
    
    /**
     * Check if file exists
     */
    suspend fun fileExists(
        userId: String,
        fileUrl: String
    ): Result<Boolean>
}