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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarrierBid
import com.example.data.model.ControlTowerSubScreen
import com.example.ui.LogisticsUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ControlTowerContainerScreen(
    uiState: LogisticsUiState,
    onSubScreenSelect: (ControlTowerSubScreen) -> Unit,
    onApproveReroute: () -> Unit,
    onRequestDockHold: () -> Unit,
    onTriggerRelay: () -> Unit,
    onSelectBid: (String) -> Unit,
    onAcceptBid: () -> Unit,
    onManualHoldWithDriver: () -> Unit,
    onEmitToast: (String) -> Unit
) {
    val subScreens = listOf(
        "Incident HUD" to (uiState.controlTowerSubScreen == ControlTowerSubScreen.DISPATCH_RADAR),
        "Cascade Auction" to (uiState.controlTowerSubScreen == ControlTowerSubScreen.CASCADING_AUCTION),
        "Consignee Hub" to (uiState.controlTowerSubScreen == ControlTowerSubScreen.CONSIGNEE_HUB)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalSurface)
    ) {
        SubScreenSelectorRow(options = subScreens) { index ->
            val selected = when (index) {
                0 -> ControlTowerSubScreen.DISPATCH_RADAR
                1 -> ControlTowerSubScreen.CASCADING_AUCTION
                else -> ControlTowerSubScreen.CONSIGNEE_HUB
            }
            onSubScreenSelect(selected)
        }

        when (uiState.controlTowerSubScreen) {
            ControlTowerSubScreen.DISPATCH_RADAR, ControlTowerSubScreen.DISPATCH_RECOVERY -> DispatchRadarScreen(
                uiState = uiState,
                onApproveReroute = onApproveReroute,
                onRequestDockHold = onRequestDockHold,
                onTriggerRelay = onTriggerRelay,
                onGoToAuction = { onSubScreenSelect(ControlTowerSubScreen.CASCADING_AUCTION) },
                onEmitToast = onEmitToast
            )
            ControlTowerSubScreen.CASCADING_AUCTION -> CascadingAuctionScreen(
                uiState = uiState,
                onSelectBid = onSelectBid,
                onAcceptBid = onAcceptBid,
                onManualOverride = onManualHoldWithDriver,
                onBackToRadar = { onSubScreenSelect(ControlTowerSubScreen.DISPATCH_RADAR) }
            )
            ControlTowerSubScreen.CONSIGNEE_HUB -> ConsigneeHubScreen(
                uiState = uiState,
                onEmitToast = onEmitToast
            )
        }
    }
}

@Composable
fun DispatchRadarScreen(
    uiState: LogisticsUiState,
    onApproveReroute: () -> Unit,
    onRequestDockHold: () -> Unit,
    onTriggerRelay: () -> Unit,
    onGoToAuction: () -> Unit,
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
        // Incident Warning Banner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.rerouteApproved) TerminalTertiaryContainer.copy(alpha = 0.2f) else TerminalErrorContainer.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.5.dp,
                    if (uiState.rerouteApproved) TerminalTertiary else TerminalError,
                    RoundedCornerShape(12.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.rerouteApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (uiState.rerouteApproved) TerminalTertiary else TerminalError,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.rerouteApproved) "PROTOCOL ALPHA ACTIVE: LOOP 12" else "CORRIDOR DELAY: I-35E MM 142.4",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (uiState.rerouteApproved) TerminalTertiary else TerminalError
                        )
                    }
                    StatusBadge(
                        text = if (uiState.rerouteApproved) "REROUTE BOUND" else "+34M BREACH",
                        type = if (uiState.rerouteApproved) BadgeType.GREEN else BadgeType.RED
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (uiState.rerouteApproved)
                        "Loop 12 Bypass pushed to Cab Nav. ETA revised to 15:38 UTC (+14m recovered). SLA 100% On-Time."
                    else
                        "Multi-vehicle incident blocking Southbound lanes. Unmitigated breach will incur Phase 2 BIND $180 demurrage penalty.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TerminalOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown to Cascading Auction
                val mins = uiState.auctionSecondsRemaining / 60
                val secs = uiState.auctionSecondsRemaining % 60
                val timerFormatted = String.format("%02d:%02d", mins, secs)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalSurfaceContainerLowest)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Phase 2 BIND Auto-Fallback Timer:", style = MaterialTheme.typography.bodySmall, color = TerminalOutline)
                    Text(text = "$timerFormatted to Cascade", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)
                }
            }
        }

        // Tactical Corridor Radar Map
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
                    Text(text = "LIVE HIGHWAY CORRIDOR RADAR", style = MaterialTheme.typography.labelSmall, color = TerminalSecondary)
                    Text(text = "Tractor #4412 • MM 138.2", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TacticalCorridorCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    isRerouted = uiState.rerouteApproved
                )
            }
        }

        // Operational Protocols Suite
        Text(text = "OPERATIONAL MITIGATION PROTOCOLS", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)

        // Protocol Alpha: Loop 12 Bypass
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.rerouteApproved) TerminalSurfaceContainerHigh else TerminalSurfaceContainer
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (uiState.rerouteApproved) TerminalTertiary else TerminalOutlineVariant,
                    RoundedCornerShape(10.dp)
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Protocol Alpha: Loop 12 Bypass", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    StatusBadge(
                        text = if (uiState.rerouteApproved) "APPROVED" else "+14 MIN RECOVERED",
                        type = if (uiState.rerouteApproved) BadgeType.GREEN else BadgeType.CYAN
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reroute via Loop 12 South bypass to avoid MM 142 bottleneck. Fuel variance +$9.40 pre-approved. ETA revised to 15:38 UTC.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TerminalOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onApproveReroute,
                    enabled = !uiState.rerouteApproved,
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.rerouteApproved) "REROUTE DISPATCHED TO CAB" else "APPROVE REROUTE & DISPATCH TO CAB",
                        style = MaterialTheme.typography.labelMedium,
                        color = TerminalOnPrimaryContainer
                    )
                }
            }
        }

        // Protocol Bravo: Bay Window Hold
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Protocol Bravo: Bay Window Hold", style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                    StatusBadge(text = if (uiState.dockHoldApproved) "HOLD CONFIRMED" else "16:00 UTC", type = BadgeType.AMBER)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transmit WMS dock reservation extension to Dallas DC #8492 Bay 08. Suppresses late demurrage charge.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TerminalOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onRequestDockHold,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.dockHoldApproved) "BAY 08 HOLD ACTIVE" else "REQUEST BAY 08 WINDOW HOLD",
                        style = MaterialTheme.typography.labelMedium,
                        color = TerminalPrimary
                    )
                }
            }
        }

        // Action to open Cascading Auction Fallback
        Button(
            onClick = onGoToAuction,
            colors = ButtonDefaults.buttonColors(containerColor = TerminalSurfaceContainerHigh),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = TerminalPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "OPEN CASCADING AUCTION DASHBOARD", style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)
        }
    }
}

@Composable
fun CascadingAuctionScreen(
    uiState: LogisticsUiState,
    onSelectBid: (String) -> Unit,
    onAcceptBid: () -> Unit,
    onManualOverride: () -> Unit,
    onBackToRadar: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Demurrage Escrow Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurfaceContainer),
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
                        Text(text = "PHASE 2 BIND SLA ENFORCEMENT", style = MaterialTheme.typography.labelSmall, color = TerminalPrimary)
                        Text(text = "CASCADING FREIGHT AUCTION", style = MaterialTheme.typography.headlineSmall, color = TerminalOnSurface)
                    }
                    StatusBadge(text = "ROUND 1 LIVE", type = BadgeType.AMBER)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Demurrage Bond", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "$180 Held", style = MaterialTheme.typography.titleMedium, color = TerminalPrimary)
                    }
                    Column {
                        Text(text = "OTP Carrier Score", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "-1.8% Risk", style = MaterialTheme.typography.titleMedium, color = TerminalError)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Broadcast Expiry", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "${uiState.auctionSecondsRemaining}s", style = MaterialTheme.typography.labelMedium, color = TerminalTertiary)
                    }
                }
            }
        }

        Text(text = "LIVE CARRIER BIDS IN CORRIDOR", style = MaterialTheme.typography.labelMedium, color = TerminalSecondary)

        // Carrier Bid Cards
        uiState.carrierBids.forEach { bid ->
            val isSelected = uiState.selectedBidId == bid.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) TerminalSurfaceContainerHigh else TerminalSurfaceContainerLow
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isSelected) TerminalPrimary else TerminalOutlineVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectBid(bid.id) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectBid(bid.id) },
                                colors = RadioButtonDefaults.colors(selectedColor = TerminalPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(text = bid.carrierName, style = MaterialTheme.typography.titleMedium, color = TerminalOnSurface)
                                Text(text = "${bid.unitNumber} • ${bid.equipmentType}", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "$${bid.bidAmountUsd}", style = MaterialTheme.typography.headlineSmall, color = TerminalPrimary)
                            if (bid.isLowest) {
                                StatusBadge(text = "LOWEST BID", type = BadgeType.GREEN)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${bid.distanceInfo} • ${bid.protocolNote}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerminalSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Actions
        Button(
            onClick = onAcceptBid,
            colors = ButtonDefaults.buttonColors(containerColor = TerminalPrimaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, tint = TerminalOnPrimaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.auctionBound) "CASCADE BOUND • CARRIER DISPATCHED" else "ACCEPT BID & DISPATCH RELAY",
                style = MaterialTheme.typography.labelLarge,
                color = TerminalOnPrimaryContainer
            )
        }

        OutlinedButton(
            onClick = onManualOverride,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = if (uiState.manualHoldWithMarcus) "HELD WITH MARCUS VANCE" else "MANUAL OVERRIDE: HOLD WITH MARCUS VANCE",
                style = MaterialTheme.typography.labelMedium,
                color = TerminalOnSurface
            )
        }
    }
}

@Composable
fun ConsigneeHubScreen(
    uiState: LogisticsUiState,
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
        // Consignee Receiving Header
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
                        Text(text = "CONSIGNEE RECEIVING HUB", style = MaterialTheme.typography.labelSmall, color = TerminalSecondary)
                        Text(text = "DALLAS DC #8492", style = MaterialTheme.typography.headlineMedium, color = TerminalOnSurface)
                    }
                    StatusBadge(text = uiState.consigneeDockBay, type = BadgeType.CYAN)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Inbound Barcode Pass:", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                    Text(text = uiState.inboundPasscode, style = MaterialTheme.typography.labelMedium, color = TerminalPrimary)
                }
            }
        }

        // Live Offload Velocity Status
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
                    Text(text = "DOCK OFFLOAD VELOCITY", style = MaterialTheme.typography.labelSmall, color = TerminalOutline)
                    Text(text = "18 / 24 Skids (75%)", style = MaterialTheme.typography.labelMedium, color = TerminalTertiary)
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 18f / 24f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TerminalTertiary,
                    trackColor = TerminalSurfaceContainerHigh
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Forklift Active", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "Unit #09 (L. Torres)", style = MaterialTheme.typography.titleSmall, color = TerminalOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Cold Chain Core", style = MaterialTheme.typography.bodySmall, color = TerminalOnSurfaceVariant)
                        Text(text = "-20.0°C Verified", style = MaterialTheme.typography.titleSmall, color = TerminalSecondary)
                    }
                }
            }
        }

        // Inbound Acceptance
        Button(
            onClick = { onEmitToast("Consignee Inbound Custody Transfer Confirmed at Bay 08") },
            colors = ButtonDefaults.buttonColors(containerColor = TerminalTertiaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.FactCheck, contentDescription = null, tint = TerminalOnTertiaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "CONFIRM INBOUND CUSTODY ACCEPTANCE", style = MaterialTheme.typography.labelLarge, color = TerminalOnTertiaryContainer)
        }
    }
}
