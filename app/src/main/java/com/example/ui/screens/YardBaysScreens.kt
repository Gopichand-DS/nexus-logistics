package com.example.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PalletSkid
import com.example.data.model.PalletStatus
import com.example.data.model.YardBaysSubScreen
import com.example.ui.LogisticsUiState
import com.example.ui.components.BadgeType
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubScreenSelectorRow
import com.example.ui.components.TacticalYardCanvas
import com.example.ui.theme.*

@Composable
fun YardBaysContainerScreen(
    uiState: LogisticsUiState,
    onSubScreenSelect: (YardBaysSubScreen) -> Unit,
    onToggleDockLock: () -> Unit,
    onSelectPallet: (PalletSkid) -> Unit,
    onSelectWorkaround: (String) -> Unit,
    onCyclePneumatics: () -> Unit,
    onEmitToast: (String) -> Unit
) {
    val subScreens = listOf(
        "Bay Navigation" to (uiState.yardBaysSubScreen == YardBaysSubScreen.BAY_NAVIGATION),
        "Safety Interlocks" to (uiState.yardBaysSubScreen == YardBaysSubScreen.INTERLOCK_FAULT),
        "Stowage Blueprint" to (uiState.yardBaysSubScreen == YardBaysSubScreen.ACTIVE_STOWAGE)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalSurface)
    ) {
        SubScreenSelectorRow(options = subScreens) { index ->
            val selected = when (index) {
                0 -> YardBaysSubScreen.BAY_NAVIGATION
                1 -> YardBaysSubScreen.INTERLOCK_FAULT
                else -> YardBaysSubScreen.ACTIVE_STOWAGE
            }
            onSubScreenSelect(selected)
        }

        when (uiState.yardBaysSubScreen) {
            YardBaysSubScreen.BAY_NAVIGATION -> BayNavigationScreen(
                uiState = uiState,
                onToggleDockLock = onToggleDockLock,
                onTriggerFault = { onSubScreenSelect(YardBaysSubScreen.INTERLOCK_FAULT) }
            )
            YardBaysSubScreen.INTERLOCK_FAULT -> InterlockFaultScreen(
                uiState = uiState,
                onSelectWorkaround = onSelectWorkaround,
                onCyclePneumatics = onCyclePneumatics,
                onBackToBay = { onSubScreenSelect(YardBaysSubScreen.BAY_NAVIGATION) }
            )
            YardBaysSubScreen.ACTIVE_STOWAGE -> ActiveStowageScreen(
                uiState = uiState,
                onSelectPallet = onSelectPallet
            )
        }
    }
}

@Composable
fun BayNavigationScreen(
    uiState: LogisticsUiState,
    onToggleDockLock: () -> Unit,
    onTriggerFault: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Target Bay Destination Card
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
                        Text(text = "ASSIGNED TARGET BAY", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                        Text(text = uiState.assignedBay, style = MaterialTheme.typography.headlineMedium, color = TerminalOnSurface)
                    }
                    StatusBadge(text = "100% STAGED", type = BadgeType.GREEN)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Sector B-North • Row C", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                    Text(text = "Pre-Cool: -20.4°C Verified", style = MaterialTheme.typography.labelSmall, color = TerminalTertiary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tactical Yard Canvas
                TacticalYardCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    isDockLockEngaged = uiState.dockLockEngaged
                )
            }
        }

        // Live TAT Timer HUD
        val mins = uiState.tatSecondsElapsed / 60
        val secs = uiState.tatSecondsElapsed % 60
        val timeFormatted = String.format("%02d:%02d", mins, secs)

        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainerLow),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CURRENT TURNAROUND TIME (TAT)", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timeFormatted,
                        style = MonospaceLarge,
                        color = TerminalPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(text = "OTP TARGET ≥ 98.5%", type = BadgeType.CYAN)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Max Allowed: 25:00 min",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerminalOnSurfaceVariant
                    )
                }
            }
        }

        // Dock Safety Protocol Checklist Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DOCK SAFETY PROTOCOL CHECKLIST",
                    style = MaterialTheme.typography.labelMedium,
                    color = TerminalPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Check Item 1: Wheel Chocks
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TerminalTertiary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Wheel Chocks & Glad Hand Lock", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                            Text(text = "Dual chock sensor seated firmly", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        }
                    }
                    StatusBadge(text = "VERIFIED", type = BadgeType.GREEN)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = TerminalOutlineVariant)

                // Check Item 2: Trailer Doors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TerminalTertiary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Trailer Doors Swung & Latched", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                            Text(text = "Full 270° latching confirmed", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        }
                    }
                    StatusBadge(text = "VERIFIED", type = BadgeType.GREEN)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = TerminalOutlineVariant)

                // Check Item 3: Dock Lock Hook (Interactive)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.dockLockEngaged) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (uiState.dockLockEngaged) TerminalTertiary else TerminalPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Dock Lock Hook Engagement", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                            Text(
                                text = if (uiState.dockLockEngaged) "RIG bar captured • Green light" else "Awaiting hydraulic lock engagement",
                                style = MaterialTheme.typography.bodySmall,
                                color = TerminalOnSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onToggleDockLock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.dockLockEngaged) TerminalTertiaryContainer else TerminalPrimaryContainer
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (uiState.dockLockEngaged) "DISENGAGE" else "ENGAGE HOOK",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (uiState.dockLockEngaged) TerminalOnTertiaryContainer else TerminalOnPrimaryContainer
                        )
                    }
                }
            }
        }

        // Fault Simulation Option
        OutlinedButton(
            onClick = onTriggerFault,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalError.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = TerminalError, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SIMULATE WHEEL CHOCK INTERLOCK FAULT", style = MaterialTheme.typography.labelSmall, color = TerminalError)
        }
    }
}

@Composable
fun InterlockFaultScreen(
    uiState: LogisticsUiState,
    onSelectWorkaround: (String) -> Unit,
    onCyclePneumatics: () -> Unit,
    onBackToBay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Red Interlock Warning
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalErrorContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, TerminalError, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dangerous, contentDescription = null, tint = TerminalError, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "DOCK INTERLOCK FAULT: BAY 14", style = MaterialTheme.typography.headlineMedium, color = TerminalError)
                        Text(text = "Hydraulic Leveler Disabled • Red Dock Light Triggered", style = MaterialTheme.typography.bodySmall, color = TerminalOnErrorContainer)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalSurfaceContainerLowest)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Fault Code #CK-04: Left Wheel Chock Sensor tripped with 6-inch gap violation. Tire contact not satisfied.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerminalOnSurface
                    )
                }
            }
        }

        Text(text = "ACTIVE RECOVERY PROTOCOLS", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)

        // Workaround Option A: Pneumatic Purge
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.selectedWorkaround == "a") TerminalSurfaceContainerHigh else TerminalSurfaceContainer
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (uiState.selectedWorkaround == "a") TerminalPrimary else TerminalOutlineVariant,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onSelectWorkaround("a") }
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Option A: Re-Cycle Pneumatic Chock", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    StatusBadge(text = "EST: 10 SEC", type = BadgeType.AMBER)
                }
                Text(
                    text = "Automated air manifold pressure purge to re-seat the ground chock sensor against the tire tread.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TerminalOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onCyclePneumatics,
                    enabled = !uiState.isPneumaticCycling,
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.isPneumaticCycling) "PURGING MANIFOLD (CYCLING)..." else "EXECUTE PNEUMATIC RE-CYCLE",
                        style = MaterialTheme.typography.labelMedium,
                        color = TerminalOnPrimaryContainer
                    )
                }
            }
        }

        // Workaround Option B: Yard Spotter
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.selectedWorkaround == "b") TerminalSurfaceContainerHigh else TerminalSurfaceContainer
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (uiState.selectedWorkaround == "b") TerminalSecondary else TerminalOutlineVariant,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onSelectWorkaround("b") }
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Option B: Dispatch Yard Spotter", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    StatusBadge(text = "EST: 2 MIN", type = BadgeType.CYAN)
                }
                Text(
                    text = "Request manual wedge repositioning by ground spotter #SP-02 stationed at Sector B.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TerminalOnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onBackToBay,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(text = "RETURN TO BAY 14 NAVIGATION", style = MaterialTheme.typography.labelMedium, color = TerminalOnSurface)
        }
    }
}

@Composable
fun ActiveStowageScreen(
    uiState: LogisticsUiState,
    onSelectPallet: (PalletSkid) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Blueprint Status Header Card
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
                        Text(text = "53FT REEFER BLUEPRINT", style = MaterialTheme.typography.labelSmall, color = TerminalSecondary)
                        Text(text = "18 / 24 SKIDS LOADED (75%)", style = MaterialTheme.typography.headlineMedium, color = TerminalOnSurface)
                    }
                    StatusBadge(text = "-20.1°C REEFER", type = BadgeType.CYAN)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Axle Weight Distribution Bars
                Text(text = "AXLE WEIGHT BALANCE METERS", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AxleWeightMeter(label = "Steer Axle", weightLbs = 11800, maxLbs = 12000, modifier = Modifier.weight(1f))
                    AxleWeightMeter(label = "Drive Tandem", weightLbs = 33400, maxLbs = 34000, modifier = Modifier.weight(1f))
                    AxleWeightMeter(label = "Trailer Tandem", weightLbs = 32900, maxLbs = 34000, modifier = Modifier.weight(1f))
                }
            }
        }

        // Interactive Floor Stowage Grid
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainerLow),
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
                    Text(text = "TRAILER FLOOR MATRIX (12 BAYS × 2 LANES)", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                    Text(text = "NOSE ◄► REAR", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 12 rows of 2 pallets (Port & Starboard)
                for (row in 0 until 12) {
                    val portIndex = row * 2
                    val starIndex = row * 2 + 1
                    val pPallet = uiState.pallets.getOrNull(portIndex)
                    val sPallet = uiState.pallets.getOrNull(starIndex)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pPallet != null) {
                            PalletGridCell(
                                pallet = pPallet,
                                isSelected = uiState.selectedPallet?.id == pPallet.id,
                                onClick = { onSelectPallet(pPallet) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (sPallet != null) {
                            PalletGridCell(
                                pallet = sPallet,
                                isSelected = uiState.selectedPallet?.id == sPallet.id,
                                onClick = { onSelectPallet(sPallet) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Selected Pallet Audit Inspector Card
        uiState.selectedPallet?.let { pallet ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TerminalPrimary, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "PALLET AUDIT INSPECTOR #${pallet.id}", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)
                        StatusBadge(text = pallet.status.name, type = BadgeType.GREEN)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "Lot Number", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = pallet.lotNumber, style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                        }
                        Column {
                            Text(text = "Tare Weight", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = "${pallet.weightLbs} lbs", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Cold Core Temp", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            Text(text = "${pallet.temperatureC}°C", style = MaterialTheme.typography.titleMedium, color = TerminalSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AxleWeightMeter(
    label: String,
    weightLbs: Int,
    maxLbs: Int,
    modifier: Modifier = Modifier
) {
    val pct = (weightLbs.toFloat() / maxLbs).coerceIn(0f, 1f)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TerminalSurfaceContainerLowest)
            .padding(8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TerminalOutline, fontSize = 9.sp)
        Text(text = "$weightLbs lbs", style = MaterialTheme.typography.labelMedium, color = TerminalOnSurface)
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (pct > 0.95f) TerminalError else TerminalTertiary,
            trackColor = TerminalSurfaceContainerHigh
        )
    }
}

@Composable
fun PalletGridCell(
    pallet: PalletSkid,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when (pallet.status) {
        PalletStatus.LOADED -> TerminalTertiary.copy(alpha = 0.2f)
        PalletStatus.IN_TRANSIT -> TerminalPrimary.copy(alpha = 0.2f)
        else -> TerminalSurfaceContainerHigh
    }
    val borderCol = if (isSelected) TerminalPrimary else TerminalOutlineVariant

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "BAY #${pallet.id}", style = MaterialTheme.typography.labelSmall, color = TerminalOnSurface)
        Text(text = "${pallet.weightLbs} lb", style = MaterialTheme.typography.labelSmall, color = TerminalOnSurfaceVariant, fontSize = 10.sp)
    }
}
