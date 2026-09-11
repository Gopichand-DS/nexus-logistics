package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val TerminalDarkColorScheme = darkColorScheme(
    primary = TerminalPrimary,
    onPrimary = TerminalOnPrimary,
    primaryContainer = TerminalPrimaryContainer,
    onPrimaryContainer = TerminalOnPrimaryContainer,
    secondary = TerminalSecondary,
    onSecondary = TerminalOnSecondary,
    secondaryContainer = TerminalSecondaryContainer,
    onSecondaryContainer = TerminalOnSecondaryContainer,
    tertiary = TerminalTertiary,
    onTertiary = TerminalOnTertiary,
    tertiaryContainer = TerminalTertiaryContainer,
    onTertiaryContainer = TerminalOnTertiaryContainer,
    background = TerminalSurface,
    onBackground = TerminalOnSurface,
    surface = TerminalSurface,
    onSurface = TerminalOnSurface,
    surfaceVariant = TerminalSurfaceContainerHighest,
    onSurfaceVariant = TerminalOnSurfaceVariant,
    surfaceContainerLowest = TerminalSurfaceContainerLowest,
    surfaceContainerLow = TerminalSurfaceContainerLow,
    surfaceContainer = TerminalSurfaceContainer,
    surfaceContainerHigh = TerminalSurfaceContainerHigh,
    surfaceContainerHighest = TerminalSurfaceContainerHighest,
    error = TerminalError,
    onError = TerminalOnError,
    errorContainer = TerminalErrorContainer,
    onErrorContainer = TerminalOnErrorContainer,
    outline = TerminalOutline,
    outlineVariant = TerminalOutlineVariant
)

@Composable
fun NexusLogisticsTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TerminalDarkColorScheme,
        typography = TerminalTypography,
        content = content
    )
}
