package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveRunSubScreen
import com.example.ui.LogisticsUiState
import com.example.ui.components.BadgeType
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubScreenSelectorRow
import com.example.ui.theme.*

@Composable
fun ActiveRunContainerScreen(
    uiState: LogisticsUiState,
    onSubScreenSelect: (ActiveRunSubScreen) -> Unit,
    onConfirmGateEntry: () -> Unit,
    onToggleTorch: () -> Unit,
    onSetExposure: (Float) -> Unit,
    onTransmitMacroPhoto: () -> Unit,
    onEmitToast: (String) -> Unit
) {
    val subScreens = listOf(
        "Gate Beacon" to (uiState.activeRunSubScreen == ActiveRunSubScreen.GATE_SCAN),
        "Discrepancy Hold" to (uiState.activeRunSubScreen == ActiveRunSubScreen.GATE_HOLD_DISCREPANCY),
        "Macro OCR" to (uiState.activeRunSubScreen == ActiveRunSubScreen.SEAL_MACRO_OCR),
        "Marshal Override" to (uiState.activeRunSubScreen == ActiveRunSubScreen.YARD_MARSHAL_OVERRIDE)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalSurface)
    ) {
        SubScreenSelectorRow(options = subScreens) { index ->
            val selected = when (index) {
                0 -> ActiveRunSubScreen.GATE_SCAN
                1 -> ActiveRunSubScreen.GATE_HOLD_DISCREPANCY
                2 -> ActiveRunSubScreen.SEAL_MACRO_OCR
                else -> ActiveRunSubScreen.YARD_MARSHAL_OVERRIDE
            }
            onSubScreenSelect(selected)
        }

        when (uiState.activeRunSubScreen) {
            ActiveRunSubScreen.GATE_SCAN -> GateOpticalBeaconScreen(
                uiState = uiState,
                onConfirmGateEntry = onConfirmGateEntry,
                onTriggerDiscrepancy = { onSubScreenSelect(ActiveRunSubScreen.GATE_HOLD_DISCREPANCY) }
            )
            ActiveRunSubScreen.GATE_HOLD_DISCREPANCY -> GateDiscrepancyHoldScreen(
                uiState = uiState,
                onGoToMacroOcr = { onSubScreenSelect(ActiveRunSubScreen.SEAL_MACRO_OCR) },
                onGoToMarshal = { onSubScreenSelect(ActiveRunSubScreen.YARD_MARSHAL_OVERRIDE) },
                onCallIntercom = { onEmitToast("Connecting Guard Intercom at South Gate Tower 3...") }
            )
            ActiveRunSubScreen.SEAL_MACRO_OCR -> SealMacroOcrScreen(
                uiState = uiState,
                onToggleTorch = onToggleTorch,
                onSetExposure = onSetExposure,
                onTransmit = onTransmitMacroPhoto
            )
            ActiveRunSubScreen.YARD_MARSHAL_OVERRIDE -> YardMarshalOverrideScreen(
                uiState = uiState,
                onApproveOverride = {
                    onEmitToast("Override Approved by Sgt. D. Kowalski • Gate 3 Cleared")
                    onConfirmGateEntry()
                }
            )
        }
    }
}

@Composable
fun GateOpticalBeaconScreen(
    uiState: LogisticsUiState,
    onConfirmGateEntry: () -> Unit,
    onTriggerDiscrepancy: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Beacon Status Card
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
                        Text(
                            text = "CONTACTLESS OPTICAL BEACON",
                            style = MaterialTheme.typography.labelMedium,
                            color = TerminalPrimary
                        )
                        Text(
                            text = "SOUTH GATE 3 • LANE 02",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TerminalOnSurface
                        )
                    }
                    StatusBadge(text = if (uiState.gateCleared) "GATE CLEARED" else "SCAN ACTIVE", type = if (uiState.gateCleared) BadgeType.GREEN else BadgeType.AMBER)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Optical QR Beacon Target Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerminalSurfaceContainerLowest)
                        .border(1.dp, TerminalPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    OpticalBeaconGraphic(progress = uiState.gateScanPct, isCleared = uiState.gateCleared)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 110.dp)
                    ) {
                        Text(
                            text = if (uiState.gateCleared) "BARRIER 03 ELEVATED 58°" else "HOLD IN CAB • SPEED < 10 MPH",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (uiState.gateCleared) TerminalTertiary else TerminalPrimary
                        )
                        Text(
                            text = "Token Refresh in ${uiState.tokenRefreshSec}s • SHA-256 Validated",
                            style = MaterialTheme.typography.bodySmall,
                            color = TerminalOutline,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Optical & RFID Pipeline", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                    Text(text = "${uiState.gateScanPct}% Verified", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { uiState.gateScanPct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TerminalPrimary,
                    trackColor = TerminalSurfaceContainerHigh,
                )
            }
        }

        // Equipment & Load Spec Readout
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainerLow),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "EQUIPMENT TELEMATICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TerminalOutline
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Tractor License", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = uiState.licensePlate, style = MaterialTheme.typography.headlineSmall, color = TerminalOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Trailer Unit", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = uiState.trailerUnit, style = MaterialTheme.typography.headlineSmall, color = TerminalSecondary)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = TerminalOutlineVariant
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Gross Weight", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "${uiState.cargoWeightLbs} lbs", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    }
                    Column {
                        Text(text = "Pallet Load", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "${uiState.palletsCount} Skids", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Origin Seal", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = uiState.boltSealExpected, style = MaterialTheme.typography.labelMedium, color = TerminalTertiary)
                    }
                }
            }
        }

        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onConfirmGateEntry,
                colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TerminalOnPrimaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.gateCleared) "INGRESS AUTHORIZED • ENTER YARD" else "CONFIRM INGRESS & LIFT BARRIER",
                    style = MaterialTheme.typography.labelLarge,
                    color = TerminalOnPrimaryContainer
                )
            }

            OutlinedButton(
                onClick = onTriggerDiscrepancy,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalError),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalError.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = TerminalError, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SIMULATE SEAL MISMATCH DISCREPANCY",
                    style = MaterialTheme.typography.labelMedium,
                    color = TerminalError
                )
            }
        }
    }
}

@Composable
fun GateDiscrepancyHoldScreen(
    uiState: LogisticsUiState,
    onGoToMacroOcr: () -> Unit,
    onGoToMarshal: () -> Unit,
    onCallIntercom: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Red Alert Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalErrorContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, TerminalError, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = TerminalError, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HOLD: MANIFEST DISCREPANCY",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TerminalError
                        )
                        Text(
                            text = "South Gate 3 • Lane 2 Ingress Paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = TerminalOnErrorContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalSurfaceContainerLowest)
                        .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Origin e-BOL Seal:", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = uiState.boltSealExpected, style = MaterialTheme.typography.labelMedium, color = TerminalTertiary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Gate Optical Cam OCR:", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = uiState.gateDetectedSeal, style = MaterialTheme.typography.labelMedium, color = TerminalError)
                        }
                        HorizontalDivider(color = TerminalOutlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "System Action:", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = "TAT CLOCK PAUSED (06:45)", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                        }
                    }
                }
            }
        }

        // Resolution Protocols
        Text(
            text = "RESOLUTION PROTOCOLS",
            style = MaterialTheme.typography.labelMedium,
            color = TerminalPrimary
        )

        // Protocol Option 1: Mobile Macro OCR
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalSecondary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { onGoToMacroOcr() }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TerminalSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TerminalSecondary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Mobile Macro OCR Scanner", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                    Text(text = "Capture seal close-up with LED illumination and 99.4% AI OCR verification", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TerminalSecondary)
            }
        }

        // Protocol Option 2: Yard Marshal Override
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(10.dp))
                .clickable { onGoToMarshal() }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TerminalPrimaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TerminalPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Yard Marshal Manual Override", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                    Text(text = "Physical bolt inspection by Sgt. D. Kowalski (#YM-08)", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TerminalPrimary)
            }
        }

        // Protocol Option 3: Guard Intercom
        OutlinedButton(
            onClick = onCallIntercom,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalOutline),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = TerminalOnSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "CALL GATE 3 SECURITY INTERCOM", style = MaterialTheme.typography.labelMedium, color = TerminalOnSurface)
        }
    }
}

@Composable
fun SealMacroOcrScreen(
    uiState: LogisticsUiState,
    onToggleTorch: () -> Unit,
    onSetExposure: (Float) -> Unit,
    onTransmit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laserScan")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "SEAL MACRO OCR VIEWFINDER", style = MaterialTheme.typography.labelMedium, color = TerminalSecondary)
                Text(text = "Target: High-Security Bolt Seal", style = MaterialTheme.typography.headlineSmall, color = TerminalOnSurface)
            }
            StatusBadge(text = "${uiState.macroOcrConfidence}% CONFIDENCE", type = BadgeType.CYAN)
        }

        // Viewfinder Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF030712))
                .border(1.5.dp, TerminalSecondary, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Corner Reticles
                val reticleLen = 30.dp.toPx()
                val reticleStroke = 3.dp.toPx()
                val pad = 24.dp.toPx()

                // Top-Left
                drawLine(TerminalSecondary, Offset(pad, pad), Offset(pad + reticleLen, pad), reticleStroke)
                drawLine(TerminalSecondary, Offset(pad, pad), Offset(pad, pad + reticleLen), reticleStroke)
                // Top-Right
                drawLine(TerminalSecondary, Offset(w - pad, pad), Offset(w - pad - reticleLen, pad), reticleStroke)
                drawLine(TerminalSecondary, Offset(w - pad, pad), Offset(w - pad, pad + reticleLen), reticleStroke)
                // Bottom-Left
                drawLine(TerminalSecondary, Offset(pad, h - pad), Offset(pad + reticleLen, h - pad), reticleStroke)
                drawLine(TerminalSecondary, Offset(pad, h - pad), Offset(pad, h - pad - reticleLen), reticleStroke)
                // Bottom-Right
                drawLine(TerminalSecondary, Offset(w - pad, h - pad), Offset(w - pad - reticleLen, h - pad), reticleStroke)
                drawLine(TerminalSecondary, Offset(w - pad, h - pad), Offset(w - pad, h - pad - reticleLen), reticleStroke)

                // Laser Scan Line
                val currentLaserY = h * laserY
                drawLine(
                    color = TerminalSecondary,
                    start = Offset(pad, currentLaserY),
                    end = Offset(w - pad, currentLaserY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Simulated Seal In Center
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TerminalSurfaceContainerHighest.copy(alpha = 0.85f))
                    .border(1.dp, TerminalSecondary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "LOCK HASP #9942", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                Text(
                    text = uiState.boltSealExpected,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TerminalTertiary
                )
                Text(text = "PARITY VERIFIED • OCR MATCH 99.4%", style = MaterialTheme.typography.labelSmall, color = TerminalTertiary)
            }
        }

        // Camera Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleTorch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isTorchOn) TerminalPrimaryContainer else TerminalSurfaceContainerHigh
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(if (uiState.isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (uiState.isTorchOn) "TORCH ON" else "TORCH OFF", style = MaterialTheme.typography.labelSmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "EV +1.2", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = uiState.exposureCompensation,
                    onValueChange = onSetExposure,
                    valueRange = 0f..2f,
                    modifier = Modifier.width(140.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = TerminalPrimary,
                        activeTrackColor = TerminalPrimary,
                        inactiveTrackColor = TerminalSurfaceContainerHigh
                    )
                )
            }
        }

        // Primary Transmit Action
        Button(
            onClick = onTransmit,
            colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TerminalOnPrimaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "TRANSMIT HIGH-RES PROOF & LIFT GATE", style = MaterialTheme.typography.labelLarge, color = TerminalOnPrimaryContainer)
        }
    }
}

@Composable
fun YardMarshalOverrideScreen(
    uiState: LogisticsUiState,
    onApproveOverride: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TerminalPrimaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = TerminalPrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "YARD MARSHAL INSPECTION", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                        Text(text = "Sgt. D. Kowalski (#YM-08)", style = MaterialTheme.typography.headlineSmall, color = TerminalOnSurface)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = TerminalOutlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Physical Bolt Inspection Checklist:", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                Spacer(modifier = Modifier.height(8.dp))

                val items = listOf(
                    "High-tensile steel cable intact with zero tampering signs" to true,
                    "Locking barrel stamp SL-884920 readable via manual magnifying lens" to true,
                    "Trailer rear door hinge pins seated & safety tags intact" to true
                )

                items.forEach { (desc, checked) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TerminalTertiary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalSurfaceContainerLow)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Barrier Gate Status:", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "ELEVATING TO 58°", style = MaterialTheme.typography.labelMedium, color = TerminalTertiary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onApproveOverride,
            colors = ButtonDefaults.buttonColors(containerColor = TerminalTertiaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = TerminalOnTertiaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "APPLY MARSHAL OVERRIDE & DISPATCH", style = MaterialTheme.typography.labelLarge, color = TerminalOnTertiaryContainer)
        }
    }
}

@Composable
fun OpticalBeaconGraphic(progress: Int, isCleared: Boolean) {
    Canvas(modifier = Modifier.size(90.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val ringColor = if (isCleared) TerminalTertiary else TerminalPrimary

        drawCircle(
            color = ringColor.copy(alpha = 0.2f),
            radius = size.width * 0.48f,
            center = center
        )
        drawCircle(
            color = ringColor,
            radius = size.width * 0.44f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = TerminalSurfaceContainerHigh,
            radius = size.width * 0.32f,
            center = center
        )

        // Center QR Target Icon
        drawRect(
            color = ringColor,
            topLeft = Offset(center.x - 12.dp.toPx(), center.y - 12.dp.toPx()),
            size = Size(24.dp.toPx(), 24.dp.toPx())
        )
        drawRect(
            color = TerminalSurface,
            topLeft = Offset(center.x - 6.dp.toPx(), center.y - 6.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx())
        )
    }
}
