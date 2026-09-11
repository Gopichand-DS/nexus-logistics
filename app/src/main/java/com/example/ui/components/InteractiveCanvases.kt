package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun TacticalYardCanvas(
    modifier: Modifier = Modifier,
    isDockLockEngaged: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TerminalSurfaceContainerLowest)
            .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Background Grid Lines (16dp spacing proportional)
            val gridStep = 24.dp.toPx()
            var x = 0f
            while (x < w) {
                drawLine(
                    color = TerminalSurfaceContainerLow,
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y < h) {
                drawLine(
                    color = TerminalSurfaceContainerLow,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // 2. Facility Boundary Wall & Sector B-North Label
            val dockY = h * 0.28f
            drawRect(
                color = TerminalSurfaceContainer,
                topLeft = Offset(0f, 0f),
                size = Size(w, dockY)
            )
            drawLine(
                color = TerminalOutline,
                start = Offset(0f, dockY),
                end = Offset(w, dockY),
                strokeWidth = 2f
            )

            // 3. Draw Dock Bays (Bays 10 to 16)
            val bayCount = 7
            val bayWidth = w / (bayCount + 1)
            for (i in 0 until bayCount) {
                val bayNumber = 10 + i
                val bayLeft = bayWidth * 0.5f + i * bayWidth
                val isTargetBay = bayNumber == 14

                if (isTargetBay) {
                    // Highlight Bay 14 in Hazard Amber
                    drawRect(
                        color = TerminalPrimaryContainer.copy(alpha = 0.35f),
                        topLeft = Offset(bayLeft, 4f),
                        size = Size(bayWidth * 0.85f, dockY - 6f)
                    )
                    drawRect(
                        color = TerminalPrimary,
                        topLeft = Offset(bayLeft, 4f),
                        size = Size(bayWidth * 0.85f, dockY - 6f),
                        style = Stroke(width = 2f)
                    )

                    // Dock Status Indicator (Green circle when ready)
                    val greenIndicatorColor = if (isDockLockEngaged) TerminalTertiary else TerminalTertiaryContainer
                    drawCircle(
                        color = greenIndicatorColor,
                        radius = 5.dp.toPx(),
                        center = Offset(bayLeft + bayWidth * 0.425f, dockY - 14f)
                    )
                } else {
                    // Standard Bay outline
                    drawRect(
                        color = TerminalSurfaceContainerHigh,
                        topLeft = Offset(bayLeft, 10f),
                        size = Size(bayWidth * 0.85f, dockY - 16f)
                    )
                    drawRect(
                        color = TerminalOutlineVariant,
                        topLeft = Offset(bayLeft, 10f),
                        size = Size(bayWidth * 0.85f, dockY - 16f),
                        style = Stroke(width = 1f)
                    )
                }
            }

            // 4. Ingress Routing Path (From South Gate 3 at bottom center-right)
            val gatePos = Offset(w * 0.78f, h - 16f)
            val waypoint1 = Offset(w * 0.78f, h * 0.65f)
            val waypoint2 = Offset(bayWidth * 4.9f, h * 0.65f)
            val targetBayEntry = Offset(bayWidth * 4.9f, dockY + 6f)

            // Ingress guide path
            val path = Path().apply {
                moveTo(gatePos.x, gatePos.y)
                lineTo(waypoint1.x, waypoint1.y)
                lineTo(waypoint2.x, waypoint2.y)
                lineTo(targetBayEntry.x, targetBayEntry.y)
            }

            drawPath(
                path = path,
                color = TerminalPrimary.copy(alpha = pulseAlpha),
                style = Stroke(
                    width = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )

            // Target Bay Parking Guide Box
            drawRect(
                color = TerminalPrimary.copy(alpha = 0.15f),
                topLeft = Offset(targetBayEntry.x - bayWidth * 0.35f, dockY + 8f),
                size = Size(bayWidth * 0.7f, h * 0.28f),
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f))
            )

            // 5. Tractor Location Beacon (at waypoint 2)
            drawCircle(
                color = TerminalPrimary.copy(alpha = pulseAlpha * 0.4f),
                radius = 16.dp.toPx(),
                center = waypoint2
            )
            drawCircle(
                color = TerminalPrimary,
                radius = 6.dp.toPx(),
                center = waypoint2
            )
            drawCircle(
                color = TerminalSurface,
                radius = 3.dp.toPx(),
                center = waypoint2
            )

            // Gate marker
            drawRect(
                color = TerminalSecondary,
                topLeft = Offset(gatePos.x - 14f, gatePos.y - 6f),
                size = Size(28f, 8f)
            )
        }
    }
}

@Composable
fun TacticalCorridorCanvas(
    modifier: Modifier = Modifier,
    isRerouted: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hazardPulse")
    val hazardAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hazardAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TerminalSurfaceContainerLowest)
            .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Grid
            val gridStep = 30.dp.toPx()
            var gx = 0f
            while (gx < w) {
                drawLine(TerminalSurfaceContainerLow, Offset(gx, 0f), Offset(gx, h), 1f)
                gx += gridStep
            }
            var gy = 0f
            while (gy < h) {
                drawLine(TerminalSurfaceContainerLow, Offset(0f, gy), Offset(w, gy), 1f)
                gy += gridStep
            }

            // 2. Primary Highway (I-35E Southbound)
            val originPos = Offset(w * 0.15f, h * 0.2f)
            val accidentPos = Offset(w * 0.52f, h * 0.2f)
            val dcPos = Offset(w * 0.88f, h * 0.78f)

            // Blocked segment of I-35E
            drawLine(
                color = TerminalOnSurfaceVariant.copy(alpha = 0.5f),
                start = originPos,
                end = Offset(w * 0.88f, h * 0.2f),
                strokeWidth = 4f
            )

            // Traffic Jam red segment
            drawLine(
                color = TerminalError,
                start = Offset(w * 0.40f, h * 0.2f),
                end = Offset(w * 0.65f, h * 0.2f),
                strokeWidth = 6f
            )

            // 3. Collision Hotspot Marker
            drawCircle(
                color = TerminalError.copy(alpha = hazardAlpha * 0.4f),
                radius = 18.dp.toPx(),
                center = accidentPos
            )
            drawCircle(
                color = TerminalError,
                radius = 7.dp.toPx(),
                center = accidentPos
            )

            // 4. Protocol Alpha: Loop 12 Bypass Route
            val detourStart = Offset(w * 0.35f, h * 0.2f)
            val detourMid1 = Offset(w * 0.38f, h * 0.55f)
            val detourMid2 = Offset(w * 0.65f, h * 0.78f)

            val bypassPath = Path().apply {
                moveTo(detourStart.x, detourStart.y)
                cubicTo(
                    detourMid1.x, detourMid1.y,
                    detourMid2.x - 40f, detourMid2.y,
                    detourMid2.x, detourMid2.y
                )
                lineTo(dcPos.x, dcPos.y)
            }

            val bypassColor = if (isRerouted) TerminalTertiary else TerminalSecondary
            drawPath(
                path = bypassPath,
                color = bypassColor,
                style = Stroke(
                    width = if (isRerouted) 5f else 3f,
                    pathEffect = if (isRerouted) null else PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                )
            )

            // 5. Origin Hub
            drawCircle(
                color = TerminalPrimary,
                radius = 8.dp.toPx(),
                center = originPos
            )

            // 6. Current Truck Position (Tractor #4412)
            val truckPos = if (isRerouted) Offset(w * 0.48f, h * 0.62f) else Offset(w * 0.32f, h * 0.2f)
            drawCircle(
                color = TerminalPrimary.copy(alpha = 0.3f),
                radius = 14.dp.toPx(),
                center = truckPos
            )
            drawCircle(
                color = TerminalPrimary,
                radius = 6.dp.toPx(),
                center = truckPos
            )

            // 7. Destination Terminal: Dallas DC #8492 (Bay 08)
            drawRect(
                color = TerminalSecondaryContainer,
                topLeft = Offset(dcPos.x - 14.dp.toPx(), dcPos.y - 14.dp.toPx()),
                size = Size(28.dp.toPx(), 28.dp.toPx()),
                style = Stroke(width = 2f)
            )
            drawRect(
                color = TerminalSecondary.copy(alpha = 0.3f),
                topLeft = Offset(dcPos.x - 12.dp.toPx(), dcPos.y - 12.dp.toPx()),
                size = Size(24.dp.toPx(), 24.dp.toPx())
            )
        }
    }
}

@Composable
fun InteractiveSignatureCanvas(
    points: List<Offset?>,
    onAddPoint: (Offset?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TerminalSurfaceContainerLowest)
            .border(1.dp, TerminalOutlineVariant, RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onAddPoint(offset) },
                    onDragEnd = { onAddPoint(null) },
                    onDragCancel = { onAddPoint(null) },
                    onDrag = { change, _ ->
                        change.consume()
                        onAddPoint(change.position)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw guideline baseline
            val baseLineY = size.height * 0.72f
            drawLine(
                color = TerminalOutlineVariant.copy(alpha = 0.6f),
                start = Offset(24f, baseLineY),
                end = Offset(size.width - 24f, baseLineY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Draw captured strokes
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                if (p1 != null && p2 != null) {
                    drawLine(
                        color = TerminalPrimary,
                        start = p1,
                        end = p2,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
