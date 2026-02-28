package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class FileMetadata(
    val id: String,
    val fileName: String,
    val originalFileName: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val fileExtension: String,
    val checksum: String? = null,
    val uploadedBy: String,
    val uploadedAt: LocalDateTime = LocalDateTime.now(),
    val lastModified: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val isPublic: Boolean = false,
    val isEncrypted: Boolean = false,
    val encryptionKey: String? = null,
    val downloadCount: Int = 0,
    val maxDownloads: Int? = null,
    val fileType: FileType = FileType.DOCUMENT,
    val category: FileCategory = FileCategory.OTHER,
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val thumbnailPath: String? = null,
    val status: FileStatus = FileStatus.ACTIVE,
    val metadata: Map<String, String> = emptyMap()
)

enum class FileType {
    DOCUMENT,
    IMAGE,
    VIDEO,
    AUDIO,
    ARCHIVE,
    SPREADSHEET,
    PRESENTATION,
    PDF,
    TEXT,
    OTHER
}

enum class FileCategory {
    PROFILE_PICTURE,
    IDENTITY_DOCUMENT,
    BANK_STATEMENT,
    RECEIPT,
    INVOICE,
    CONTRACT,
    REPORT,
    BACKUP,
    TEMPORARY,
    OTHER
}

enum class FileStatus {
    UPLOADING,
    ACTIVE,
    ARCHIVED,
    DELETED,
    CORRUPTED,
    EXPIRED
}