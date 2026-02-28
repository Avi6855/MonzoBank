package com.avinashpatil.app.monzobank.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ExportOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val estimatedSize: String,
    val isSelected: Boolean = false,
    val isRecommended: Boolean = false
)

data class ExportFormat(
    val id: String,
    val name: String,
    val description: String,
    val fileExtension: String,
    val icon: ImageVector
)

data class ExportRequest(
    val id: String,
    val title: String,
    val status: ExportStatus,
    val requestDate: String,
    val completedDate: String?,
    val downloadUrl: String?,
    val fileSize: String?,
    val expiryDate: String?
)

data class DataCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val dataTypes: List<String>,
    val isIncluded: Boolean = true
)

enum class ExportStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, EXPIRED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataScreen(
    onBackClick: () -> Unit,
    onStartExportClick: (List<String>, String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onDeleteRequestClick: (String) -> Unit
) {
    val exportOptions = remember { getDummyExportOptions() }
    val exportFormats = remember { getDummyExportFormats() }
    val exportRequests = remember { getDummyExportRequests() }
    val dataCategories = remember { getDummyDataCategories() }
    var selectedOptions by remember { mutableStateOf(exportOptions.filter { it.isSelected }.map { it.id }) }
    var selectedFormat by remember { mutableStateOf("pdf") }
    var selectedCategories by remember { mutableStateOf(dataCategories.filter { it.isIncluded }.map { it.id }) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("New Export", "Export History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export My Data") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Default.Help, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> NewExportContent(
                    exportOptions = exportOptions,
                    exportFormats = exportFormats,
                    dataCategories = dataCategories,
                    selectedOptions = selectedOptions,
                    selectedFormat = selectedFormat,
                    selectedCategories = selectedCategories,
                    onOptionToggle = { optionId ->
                        selectedOptions = if (selectedOptions.contains(optionId)) {
                            selectedOptions - optionId
                        } else {
                            selectedOptions + optionId
                        }
                    },
                    onFormatChange = { selectedFormat = it },
                    onCategoryToggle = { categoryId ->
                        selectedCategories = if (selectedCategories.contains(categoryId)) {
                            selectedCategories - categoryId
                        } else {
                            selectedCategories + categoryId
                        }
                    },
                    onStartExport = { onStartExportClick(selectedOptions, selectedFormat) }
                )
                1 -> ExportHistoryContent(
                    requests = exportRequests,
                    onDownloadClick = onDownloadClick,
                    onDeleteClick = onDeleteRequestClick
                )
            }
        }
    }
}

@Composable
fun NewExportContent(
    exportOptions: List<ExportOption>,
    exportFormats: List<ExportFormat>,
    dataCategories: List<DataCategory>,
    selectedOptions: List<String>,
    selectedFormat: String,
    selectedCategories: List<String>,
    onOptionToggle: (String) -> Unit,
    onFormatChange: (String) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onStartExport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Introduction
        item {
            ExportIntroCard()
        }

        // Data Categories
        item {
            Text(
                text = "Select Data Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(dataCategories) { category ->
            DataCategoryCard(
                category = category,
                isSelected = selectedCategories.contains(category.id),
                onToggle = { onCategoryToggle(category.id) }
            )
        }

        // Export Options
        item {
            Text(
                text = "Export Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(exportOptions) { option ->
            ExportOptionCard(
                option = option,
                isSelected = selectedOptions.contains(option.id),
                onToggle = { onOptionToggle(option.id) }
            )
        }

        // Format Selection
        item {
            Text(
                text = "Export Format",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            FormatSelectionCard(
                formats = exportFormats,
                selectedFormat = selectedFormat,
                onFormatChange = onFormatChange
            )
        }

        // Privacy Notice
        item {
            PrivacyNoticeCard()
        }

        // Export Summary
        item {
            ExportSummaryCard(
                selectedOptionsCount = selectedOptions.size,
                selectedCategoriesCount = selectedCategories.size,
                selectedFormat = exportFormats.find { it.id == selectedFormat }?.name ?: "Unknown"
            )
        }

        // Start Export Button
        item {
            Button(
                onClick = onStartExport,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedOptions.isNotEmpty() && selectedCategories.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Export")
            }
        }

        // Important Notes
        item {
            ImportantNotesCard()
        }
    }
}

@Composable
fun ExportHistoryContent(
    requests: List<ExportRequest>,
    onDownloadClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Export History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (requests.isNotEmpty()) {
            items(requests) { request ->
                ExportRequestCard(
                    request = request,
                    onDownloadClick = { onDownloadClick(request.id) },
                    onDeleteClick = { onDeleteClick(request.id) }
                )
            }
        } else {
            item {
                EmptyExportHistoryState()
            }
        }

        // Export Guidelines
        item {
            ExportGuidelinesCard()
        }
    }
}

@Composable
fun ExportIntroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Export Your Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Download a copy of your account data for your records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DataCategoryCard(
    category: DataCategory,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Includes: ${category.dataTypes.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun ExportOptionCard(
    option: ExportOption,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (option.isRecommended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Recommended",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Estimated size: ${option.estimatedSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun FormatSelectionCard(
    formats: List<ExportFormat>,
    selectedFormat: String,
    onFormatChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Choose Export Format",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            formats.forEach { format ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFormatChange(format.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedFormat == format.id,
                        onClick = { onFormatChange(format.id) }
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = format.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "${format.name} (.${format.fileExtension})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = format.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportRequestCard(
    request: ExportRequest,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Requested: ${request.requestDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                ExportStatusChip(status = request.status)
            }
            
            if (request.completedDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Completed: ${request.completedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (request.fileSize != null) {
                Text(
                    text = "File size: ${request.fileSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (request.expiryDate != null) {
                Text(
                    text = "Expires: ${request.expiryDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF9800)
                )
            }
            
            if (request.status == ExportStatus.COMPLETED && request.downloadUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download")
                    }
                    
                    OutlinedButton(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PrivacyTip,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Privacy Notice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Your exported data will be securely encrypted and available for download for 30 days.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ExportSummaryCard(
    selectedOptionsCount: Int,
    selectedCategoriesCount: Int,
    selectedFormat: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Export Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Data Categories:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$selectedCategoriesCount selected",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Export Options:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$selectedOptionsCount selected",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Format:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = selectedFormat,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ImportantNotesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Important Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val notes = listOf(
                "Export processing may take up to 24 hours",
                "You'll receive an email when your export is ready",
                "Downloads expire after 30 days for security",
                "Large exports may be split into multiple files"
            )
            
            notes.forEach { note ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ExportGuidelinesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Export Guidelines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val guidelines = listOf(
                "You can request up to 3 exports per month",
                "Exports are automatically deleted after 30 days",
                "Contact support if you need older data",
                "All exports are encrypted for your security"
            )
            
            guidelines.forEach { guideline ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = guideline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ExportStatusChip(status: ExportStatus) {
    val (color, text) = when (status) {
        ExportStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        ExportStatus.PROCESSING -> Color.Blue to "Processing"
        ExportStatus.COMPLETED -> Color.Green to "Completed"
        ExportStatus.FAILED -> Color.Red to "Failed"
        ExportStatus.EXPIRED -> Color.Gray to "Expired"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyExportHistoryState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Export History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You haven't requested any data exports yet. Create your first export to see it here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummyExportOptions(): List<ExportOption> {
    return listOf(
        ExportOption(
            id = "complete_history",
            title = "Complete Account History",
            description = "All transactions, statements, and account activity",
            icon = Icons.Default.History,
            estimatedSize = "15-25 MB",
            isSelected = true,
            isRecommended = true
        ),
        ExportOption(
            id = "personal_data",
            title = "Personal Information",
            description = "Profile details, contact information, and preferences",
            icon = Icons.Default.Person,
            estimatedSize = "< 1 MB",
            isSelected = true
        ),
        ExportOption(
            id = "documents",
            title = "Documents & Statements",
            description = "Monthly statements, tax documents, and certificates",
            icon = Icons.Default.Description,
            estimatedSize = "5-10 MB",
            isSelected = false
        ),
        ExportOption(
            id = "communication",
            title = "Communication History",
            description = "Support conversations, notifications, and messages",
            icon = Icons.Default.Chat,
            estimatedSize = "2-5 MB",
            isSelected = false
        )
    )
}

fun getDummyExportFormats(): List<ExportFormat> {
    return listOf(
        ExportFormat(
            id = "pdf",
            name = "PDF",
            description = "Portable document format, easy to view and print",
            fileExtension = "pdf",
            icon = Icons.Default.PictureAsPdf
        ),
        ExportFormat(
            id = "csv",
            name = "CSV",
            description = "Comma-separated values, good for spreadsheets",
            fileExtension = "csv",
            icon = Icons.Default.TableChart
        ),
        ExportFormat(
            id = "json",
            name = "JSON",
            description = "JavaScript Object Notation, machine-readable format",
            fileExtension = "json",
            icon = Icons.Default.Code
        )
    )
}

fun getDummyExportRequests(): List<ExportRequest> {
    return listOf(
        ExportRequest(
            id = "EXP001",
            title = "Complete Account History",
            status = ExportStatus.COMPLETED,
            requestDate = "2 days ago",
            completedDate = "1 day ago",
            downloadUrl = "https://example.com/download/exp001",
            fileSize = "18.5 MB",
            expiryDate = "29 days"
        ),
        ExportRequest(
            id = "EXP002",
            title = "Personal Information Export",
            status = ExportStatus.PROCESSING,
            requestDate = "3 hours ago",
            completedDate = null,
            downloadUrl = null,
            fileSize = null,
            expiryDate = null
        )
    )
}

fun getDummyDataCategories(): List<DataCategory> {
    return listOf(
        DataCategory(
            id = "transactions",
            name = "Transactions",
            description = "All your payment and transfer history",
            icon = Icons.Default.Payment,
            dataTypes = listOf("Payments", "Transfers", "Direct Debits", "Standing Orders"),
            isIncluded = true
        ),
        DataCategory(
            id = "account_info",
            name = "Account Information",
            description = "Account details and balances",
            icon = Icons.Default.AccountBalance,
            dataTypes = listOf("Account Numbers", "Balances", "Account Types"),
            isIncluded = true
        ),
        DataCategory(
            id = "personal_data",
            name = "Personal Data",
            description = "Your profile and contact information",
            icon = Icons.Default.Person,
            dataTypes = listOf("Name", "Address", "Phone", "Email", "Preferences"),
            isIncluded = true
        ),
        DataCategory(
            id = "cards",
            name = "Cards & Payments",
            description = "Card details and payment methods",
            icon = Icons.Default.CreditCard,
            dataTypes = listOf("Card Details", "Payment Methods", "Limits"),
            isIncluded = false
        ),
        DataCategory(
            id = "investments",
            name = "Investments",
            description = "Investment portfolio and trading history",
            icon = Icons.Default.TrendingUp,
            dataTypes = listOf("Portfolio", "Trades", "Performance", "Dividends"),
            isIncluded = false
        )
    )
}