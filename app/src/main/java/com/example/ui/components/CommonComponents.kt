package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OperationalTab
import com.example.ui.LogisticsUiState
import com.example.ui.theme.*

@Composable
fun TopOperationalHud(
    uiState: LogisticsUiState,
    onEmergencySosClick: () -> Unit
) {
    Surface(
        color = TerminalSurfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Run Identifier
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TerminalTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXUS LOGISTICS",
                                style = MaterialTheme.typography.labelMedium,
                                color = TerminalPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•  ${uiState.runId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TerminalOnSurfaceVariant
                            )
                        }
                        Text(
                            text = uiState.orderNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = TerminalOutline
                        )
                    }
                }

                // Quick Status & Emergency SOS Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TerminalSurfaceContainerLow)
                            .border(1.dp, TerminalSecondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "5G LIVE • GEOFENCE ON",
                            style = MaterialTheme.typography.labelSmall,
                            color = TerminalSecondary,
                            fontSize = 9.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onEmergencySosClick,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(TerminalErrorContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "SOS Emergency",
                            tint = TerminalError,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomOperationalNavigationBar(
    activeTab: OperationalTab,
    onTabSelected: (OperationalTab) -> Unit
) {
    Surface(
        color = TerminalSurfaceContainerLowest,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OperationalTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                val icon: ImageVector = when (tab) {
                    OperationalTab.ACTIVE_RUN -> Icons.AutoMirrored.Filled.AltRoute
                    OperationalTab.YARD_BAYS -> Icons.Default.Warehouse
                    OperationalTab.DOCS_EPOP -> Icons.Default.CheckCircle
                    OperationalTab.CONTROL_TOWER -> Icons.Default.CellTower
                }

                val contentColor = if (isSelected) TerminalPrimary else TerminalOutline
                val bgColor = if (isSelected) TerminalSurfaceContainerHigh else Color.Transparent

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    type: BadgeType = BadgeType.AMBER
) {
    val (bg, stroke, fg) = when (type) {
        BadgeType.AMBER -> Triple(
            TerminalPrimaryContainer.copy(alpha = 0.15f),
            TerminalPrimary.copy(alpha = 0.5f),
            TerminalPrimary
        )
        BadgeType.CYAN -> Triple(
            TerminalSecondary.copy(alpha = 0.15f),
            TerminalSecondary.copy(alpha = 0.5f),
            TerminalSecondary
        )
        BadgeType.GREEN -> Triple(
            TerminalTertiary.copy(alpha = 0.15f),
            TerminalTertiary.copy(alpha = 0.5f),
            TerminalTertiary
        )
        BadgeType.RED -> Triple(
            TerminalErrorContainer.copy(alpha = 0.25f),
            TerminalError.copy(alpha = 0.6f),
            TerminalError
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, stroke, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontSize = 10.sp
        )
    }
}

enum class BadgeType {
    AMBER,
    CYAN,
    GREEN,
    RED
}

@Composable
fun SubScreenSelectorRow(
    options: List<Pair<String, Boolean>>,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, (label, isSelected) ->
            val bg = if (isSelected) TerminalPrimaryContainer.copy(alpha = 0.25f) else TerminalSurfaceContainerLow
            val border = if (isSelected) TerminalPrimary else TerminalOutlineVariant
            val textCol = if (isSelected) TerminalPrimary else TerminalOnSurfaceVariant

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(6.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textCol,
                    fontSize = 11.sp
                )
            }
        }
    }
}
