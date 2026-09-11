package com.example.data.model

import androidx.compose.ui.geometry.Offset

enum class OperationalTab(val label: String, val iconName: String) {
    ACTIVE_RUN("Active Run", "alt_route"),
    YARD_BAYS("Yard & Bays", "warehouse"),
    DOCS_EPOP("Docs / e-POP", "verified"),
    CONTROL_TOWER("Control Tower", "cell_tower")
}

enum class ActiveRunSubScreen {
    GATE_SCAN,
    GATE_HOLD_DISCREPANCY,
    SEAL_MACRO_OCR,
    YARD_MARSHAL_OVERRIDE
}

enum class YardBaysSubScreen {
    BAY_NAVIGATION,
    INTERLOCK_FAULT,
    ACTIVE_STOWAGE
}

enum class ControlTowerSubScreen {
    DISPATCH_RADAR,
    DISPATCH_RECOVERY,
    CASCADING_AUCTION,
    CONSIGNEE_HUB
}

enum class PalletStatus {
    LOADED,
    IN_TRANSIT,
    STAGED,
    NOSE,
    OFFLOADED
}

data class PalletSkid(
    val id: Int,
    val lotNumber: String,
    val weightLbs: Int,
    val temperatureC: Double,
    val status: PalletStatus,
    val description: String = "Organic FMCG Produce"
)

data class CarrierBid(
    val id: String,
    val carrierName: String,
    val unitNumber: String,
    val etaMinutes: Int,
    val distanceInfo: String,
    val bidAmountUsd: Int,
    val isLowest: Boolean,
    val equipmentType: String,
    val protocolNote: String
)

data class SafetyCheckItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isVerified: Boolean,
    val isInteractive: Boolean = false,
    val actionText: String = ""
)

data class DispatchEvent(
    val timeUtc: String,
    val description: String,
    val severity: String = "INFO"
)

data class DrawPoint(
    val offset: Offset
)
