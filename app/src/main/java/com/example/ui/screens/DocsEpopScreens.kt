package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LogisticsUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DocsEpopContainerScreen(
    uiState: LogisticsUiState,
    onAddSignaturePoint: (Offset?) -> Unit,
    onClearSignature: () -> Unit,
    onToggleBiometric: () -> Unit,
    onCompletePickup: () -> Unit,
    onEmitToast: (String) -> Unit
) {
    var showFullBolViewer by remember { mutableStateOf(false) }

    val subScreens = listOf(
        "e-POP Signature" to !showFullBolViewer,
        "Cryptographic e-BOL" to showFullBolViewer
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalSurface)
    ) {
        SubScreenSelectorRow(options = subScreens) { index ->
            showFullBolViewer = (index == 1)
        }

        if (!showFullBolViewer) {
            EpopSignatureScreen(
                uiState = uiState,
                onAddSignaturePoint = onAddSignaturePoint,
                onClearSignature = onClearSignature,
                onToggleBiometric = onToggleBiometric,
                onCompletePickup = onCompletePickup,
                onViewFullBol = { showFullBolViewer = true }
            )
        } else {
            CryptographicBolViewerScreen(
                uiState = uiState,
                onBack = { showFullBolViewer = false },
                onEmitToast = onEmitToast
            )
        }
    }
}

@Composable
fun EpopSignatureScreen(
    uiState: LogisticsUiState,
    onAddSignaturePoint: (Offset?) -> Unit,
    onClearSignature: () -> Unit,
    onToggleBiometric: () -> Unit,
    onCompletePickup: () -> Unit,
    onViewFullBol: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SLA Bonus Incentive Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalTertiaryContainer.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, TerminalTertiary, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    StatusBadge(text = "SLA BONUS QUALIFIED", type = BadgeType.GREEN)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "+$45.00 PAYOUT CREDIT", style = MaterialTheme.typography.headlineMedium, color = TerminalTertiary)
                    Text(text = "Turnaround Time: 19m 40s (Target: ≤ 25m)", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(TerminalTertiary.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TerminalTertiary, modifier = Modifier.size(28.dp))
                }
            }
        }

        // Load & Verification Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "CHAIN OF CUSTODY VERIFICATION", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Manifest Load", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = uiState.manifestId, style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    }
                    Column {
                        Text(text = "Bolt Seal Stamp", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = uiState.boltSealExpected, style = MaterialTheme.typography.titleMedium, color = TerminalTertiary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Pallet Staging", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "24 / 24 Locked", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    }
                }
            }
        }

        // Digital Signature Pad Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainerLow),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "DRIVER CUSTODY SIGNOFF", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)
                        Text(text = "Marcus Vance (Nexus Tier-1)", style = MaterialTheme.typography.headlineSmall, color = TerminalOnSurface)
                    }
                    TextButton(onClick = onClearSignature) {
                        Text(text = "CLEAR", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.isFastPassBiometric) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TerminalTertiary.copy(alpha = 0.1f))
                            .border(1.dp, TerminalTertiary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TerminalTertiary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "BIOMETRIC FASTPASS VERIFIED", style = MaterialTheme.typography.titleMedium, color = TerminalTertiary)
                                Text(text = "Key #NX-BIO-99201 Authenticated", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            }
                        }
                    }
                } else {
                    InteractiveSignatureCanvas(
                        points = uiState.signaturePoints,
                        onAddPoint = onAddSignaturePoint,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FastPass Biometric Toggle Button
                OutlinedButton(
                    onClick = onToggleBiometric,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TerminalSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isFastPassBiometric) "SWITCH TO MANUAL SIGNATURE" else "USE INSTANT BIOMETRIC FASTPASS",
                        style = MaterialTheme.typography.labelMedium,
                        color = TerminalSecondary
                    )
                }
            }
        }

        // SHA-256 Ledger Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "SHA-256 CRYPTOGRAPHIC PROOF", style = MaterialTheme.typography.labelSmall, color = TerminalOutline, fontSize = 9.sp)
                Text(text = "0x8f9c...d402e (Immutable Ledger)", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant, fontSize = 11.sp)
            }
            TextButton(onClick = onViewFullBol) {
                Text(text = "VIEW FULL e-BOL", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
            }
        }

        // Complete Pickup Action
        Button(
            onClick = onCompletePickup,
            colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = TerminalOnPrimaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.outboundDispatched) "OUTBOUND ACTIVE • TRACKING ENGAGED" else "COMPLETE PICKUP & TRIGGER OUTBOUND",
                style = MaterialTheme.typography.labelLarge,
                color = TerminalOnPrimaryContainer
            )
        }
    }
}

@Composable
fun CryptographicBolViewerScreen(
    uiState: LogisticsUiState,
    onBack: () -> Unit,
    onEmitToast: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "ELECTRONIC BILL OF LADING", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                        Text(text = "e-BOL #${uiState.orderNumber}", style = MaterialTheme.typography.headlineMedium, color = TerminalOnSurface)
                    }
                    StatusBadge(text = "LEDGER STAMPED", type = BadgeType.GREEN)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = TerminalOutlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Origin Facility: Dallas North Logistics FC (Bay 14)", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                Text(text = "Destination: Target Regional DC Dallas #8492 (Bay 08)", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                Text(text = "Carrier Assigned: Marcus Vance • Tractor #4412 • Trailer #TL-9023", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                Text(text = "Seal Authenticated: ${uiState.boltSealExpected} (Tamper Evident)", style = MaterialTheme.typography.bodySmall, color = TerminalTertiary)
                Text(text = "Gross Weight: 42,300 lbs • 24 Standard 48x40 FMCG Skids", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                Text(text = "Cold Chain Requirement: Continuous -20.0°C Reefer", style = MaterialTheme.typography.bodySmall, color = TerminalSecondary)

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalSurfaceContainerLowest)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(text = "SHA-256 HASH VERIFICATION:", style = MaterialTheme.typography.labelSmall, color = TerminalOutline, fontSize = 9.sp)
                        Text(
                            text = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                            style = MaterialTheme.typography.bodySmall,
                            color = TerminalPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "BACK TO SIGNATURE", style = MaterialTheme.typography.labelSmall, color = TerminalOnSurface)
            }
            Button(
                onClick = { onEmitToast("Cryptographic e-BOL Exported to PDF") },
                colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = TerminalOnPrimaryContainer)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "EXPORT PDF", style = MaterialTheme.typography.labelSmall, color = TerminalOnPrimaryContainer)
            }
        }
    }
}
