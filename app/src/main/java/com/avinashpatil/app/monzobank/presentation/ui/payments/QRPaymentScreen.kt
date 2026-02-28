package com.avinashpatil.app.monzobank.presentation.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRPaymentScreen(
    accounts: List<Account>,
    onNavigateBack: () -> Unit,
    onQRScanned: (QRPaymentData) -> Unit,
    onGenerateQR: (QRGenerationData) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Scan", "Receive")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "QR Payments",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MonzoCoralPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        // Content
        when (selectedTab) {
            0 -> QRScannerTab(onQRScanned = onQRScanned)
            1 -> QRGeneratorTab(
                accounts = accounts,
                onGenerateQR = onGenerateQR
            )
        }
    }
}

@Composable
fun QRScannerTab(
    onQRScanned: (QRPaymentData) -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf<QRPaymentData?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!hasPermission) {
            // Permission Request UI
            PermissionRequestCard(
                onRequestPermission = { hasPermission = true }
            )
        } else {
            // Camera Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        // Mock camera preview
                        CameraPreviewPlaceholder()
                        
                        // Scanning overlay
                        QRScanningOverlay()
                    } else {
                        // Start scanning button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.White
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { isScanning = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MonzoCoralPrimary
                                )
                            ) {
                                Text("Start Scanning")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Instructions
            InstructionsCard(
                title = "How to scan",
                instructions = listOf(
                    "Point your camera at a QR code",
                    "Make sure the code is clearly visible",
                    "Hold steady until the code is recognized",
                    "Review payment details before confirming"
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock scan button for demo
            Button(
                onClick = {
                    val mockData = QRPaymentData(
                        merchantName = "Coffee Shop",
                        amount = BigDecimal("4.50"),
                        reference = "Coffee & Pastry",
                        merchantId = "MERCHANT_123"
                    )
                    onQRScanned(mockData)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralSecondary
                )
            ) {
                Text("Simulate QR Scan (Demo)")
            }
        }
    }
}

@Composable
fun QRGeneratorTab(
    accounts: List<Account>,
    onGenerateQR: (QRGenerationData) -> Unit
) {
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var generatedQR by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Receive to Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                accounts.forEach { account ->
                    AccountSelectionRow(
                        account = account,
                        isSelected = selectedAccount?.id == account.id,
                        onClick = { selectedAccount = account }
                    )
                    
                    if (account != accounts.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
        
        // Amount and Description
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (£) - Optional") },
                    placeholder = { Text("Leave empty for any amount") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description - Optional") },
                    placeholder = { Text("What's this payment for?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        }
        
        // Generate QR Button
        Button(
            onClick = {
                selectedAccount?.let { account ->
                    val qrData = QRGenerationData(
                        account = account,
                        amount = amount.toBigDecimalOrNull(),
                        description = description.ifBlank { null }
                    )
                    onGenerateQR(qrData)
                    generatedQR = "QR_CODE_DATA_${System.currentTimeMillis()}"
                }
            },
            enabled = selectedAccount != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonzoCoralPrimary
            )
        ) {
            Text(
                text = "Generate QR Code",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        // Generated QR Code Display
        generatedQR?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your QR Code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // QR Code Placeholder
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                Color.Black,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                2.dp,
                                Color.Gray,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "QR\nCODE",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Show this code to receive payment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    if (amount.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Amount: £${amount}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MonzoCoralPrimary
                        )
                    }
                    
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestCard(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MonzoCoralPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "To scan QR codes, we need access to your camera. This allows you to quickly pay merchants by scanning their QR codes.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Text("Grant Camera Permission")
            }
        }
    }
}

@Composable
fun CameraPreviewPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera Preview\n(Mock)",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun QRScanningOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Scanning frame
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    3.dp,
                    MonzoCoralPrimary,
                    RoundedCornerShape(16.dp)
                )
        )
        
        // Corner indicators
        Box(
            modifier = Modifier.size(250.dp)
        ) {
            // Top-left corner
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.TopStart)
                    .border(
                        3.dp,
                        Color.White,
                        RoundedCornerShape(topStart = 16.dp)
                    )
            )
            
            // Top-right corner
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.TopEnd)
                    .border(
                        3.dp,
                        Color.White,
                        RoundedCornerShape(topEnd = 16.dp)
                    )
            )
            
            // Bottom-left corner
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomStart)
                    .border(
                        3.dp,
                        Color.White,
                        RoundedCornerShape(bottomStart = 16.dp)
                    )
            )
            
            // Bottom-right corner
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd)
                    .border(
                        3.dp,
                        Color.White,
                        RoundedCornerShape(bottomEnd = 16.dp)
                    )
            )
        }
    }
}

@Composable
fun InstructionsCard(
    title: String,
    instructions: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            instructions.forEachIndexed { index, instruction ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MonzoCoralPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AccountSelectionRow(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MonzoCoralPrimary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MonzoCoralPrimary
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = account.formattedBalance,
                style = MaterialTheme.typography.bodySmall,
                color = MonzoCoralPrimary
            )
        }
    }
}

data class QRPaymentData(
    val merchantName: String,
    val amount: BigDecimal?,
    val reference: String?,
    val merchantId: String
)

data class QRGenerationData(
    val account: Account,
    val amount: BigDecimal?,
    val description: String?
)