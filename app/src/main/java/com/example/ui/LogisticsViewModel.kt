package com.example.ui

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LogisticsUiState(
    // Tab & Navigation
    val activeTab: OperationalTab = OperationalTab.ACTIVE_RUN,
    val activeRunSubScreen: ActiveRunSubScreen = ActiveRunSubScreen.GATE_SCAN,
    val yardBaysSubScreen: YardBaysSubScreen = YardBaysSubScreen.BAY_NAVIGATION,
    val controlTowerSubScreen: ControlTowerSubScreen = ControlTowerSubScreen.DISPATCH_RADAR,

    // Shipment & Hardware Data
    val orderNumber: String = "ORD-2024-912B",
    val runId: String = "TRK-9842",
    val tractorUnit: String = "#4412",
    val licensePlate: String = "TX 882-J9K",
    val trailerUnit: String = "#TL-9023",
    val manifestId: String = "NX-88204",
    val boltSealExpected: String = "SL-884920",
    val gateDetectedSeal: String = "TK-99201",
    val cargoWeightLbs: Int = 42300,
    val palletsCount: Int = 24,
    val reeferTempC: Double = -20.1,
    val reeferSetpointC: Double = -20.0,

    // Gate Telematics & Scans
    val gateScanPct: Int = 78,
    val isGateScanning: Boolean = true,
    val tokenRefreshSec: Int = 38,
    val gateCleared: Boolean = false,
    val barrierAngle: Int = 0,

    // Seal Camera Macro Viewfinder
    val macroOcrConfidence: Float = 99.4f,
    val exposureCompensation: Float = 1.2f,
    val isTorchOn: Boolean = true,
    val isSealPhotoTransmitted: Boolean = false,
    val isOverrideApproved: Boolean = false,

    // Turnaround Time & Bay
    val tatSecondsElapsed: Int = 405, // 06:45
    val tatTargetMinutes: Int = 25,
    val tatPaused: Boolean = false,
    val dockLockEngaged: Boolean = false,
    val assignedBay: String = "DOCK BAY 14",
    val bayReadyPercent: Int = 100,

    // Interlock Fault & Workaround
    val isChockFaultActive: Boolean = false,
    val selectedWorkaround: String = "a",
    val isPneumaticCycling: Boolean = false,
    val pneumaticCountdown: Int = 10,

    // Stowage Blueprint (24 pallets)
    val pallets: List<PalletSkid> = emptyList(),
    val selectedPallet: PalletSkid? = null,
    val offloadedCount: Int = 18,

    // e-POP Signoff
    val signaturePoints: List<Offset?> = emptyList(),
    val isFastPassBiometric: Boolean = false,
    val isCustodySigned: Boolean = false,
    val outboundDispatched: Boolean = false,
    val slaBonusAmount: Double = 45.00,

    // Dispatch Radar & Protocol Alpha Reroute
    val corridorDelayMinutes: Int = 34,
    val rerouteApproved: Boolean = false,
    val revisedEtaUtc: String = "15:38",
    val dockHoldApproved: Boolean = false,
    val relayMobilized: Boolean = false,
    val emergencyExecuted: Boolean = false,

    // Cascading Auction
    val auctionSecondsRemaining: Int = 222,
    val selectedBidId: String = "apex-771",
    val auctionBound: Boolean = false,
    val manualHoldWithMarcus: Boolean = false,
    val carrierBids: List<CarrierBid> = emptyList(),

    // Consignee Receiving (Dallas DC #8492)
    val consigneeDockBay: String = "BAY 08",
    val inboundPasscode: String = "NX-882-G8"
)

class LogisticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LogisticsUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        initializePalletData()
        initializeBids()
        startTimers()
    }

    private fun initializePalletData() {
        val initialPallets = (1..24).map { index ->
            val status = when {
                index in 1..16 -> PalletStatus.LOADED
                index in 17..18 -> PalletStatus.IN_TRANSIT
                else -> PalletStatus.NOSE
            }
            val weight = 1720 + (index * 13) % 120
            PalletSkid(
                id = index,
                lotNumber = "LOT #TX-902${10 + index}",
                weightLbs = weight,
                temperatureC = -20.1 + (index % 3) * 0.1,
                status = status
            )
        }
        _uiState.update { it.copy(pallets = initialPallets, selectedPallet = initialPallets[9]) }
    }

    private fun initializeBids() {
        val bids = listOf(
            CarrierBid(
                id = "apex-771",
                carrierName = "Apex Logistics",
                unitNumber = "Unit #771",
                etaMinutes = 12,
                distanceInfo = "12 Mins Away (MM 142)",
                bidAmountUsd = 420,
                isLowest = true,
                equipmentType = "53ft Reefer Bobtail",
                protocolNote = "Protocol: Immediate Drop & Hook"
            ),
            CarrierBid(
                id = "lonestar-2",
                carrierName = "Lone Star Freight",
                unitNumber = "Unit #550",
                etaMinutes = 18,
                distanceInfo = "18 Mins Away (MM 135)",
                bidAmountUsd = 460,
                isLowest = false,
                equipmentType = "53ft Reefer/Dry",
                protocolNote = "Standby #2"
            ),
            CarrierBid(
                id = "swiftline-3",
                carrierName = "SwiftLine Intermodal",
                unitNumber = "Unit #109",
                etaMinutes = 24,
                distanceInfo = "24 Mins Away (Spur 366)",
                bidAmountUsd = 495,
                isLowest = false,
                equipmentType = "Dedicated Reefer",
                protocolNote = "Standby #3"
            )
        )
        _uiState.update { it.copy(carrierBids = bids) }
    }

    private fun startTimers() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    val newTat = if (!state.tatPaused) state.tatSecondsElapsed + 1 else state.tatSecondsElapsed
                    val newTokenSec = if (state.tokenRefreshSec <= 1) 45 else state.tokenRefreshSec - 1
                    val newAuctionSec = if (state.auctionSecondsRemaining > 0 && !state.auctionBound) state.auctionSecondsRemaining - 1 else state.auctionSecondsRemaining
                    val newGatePct = if (state.gateScanPct < 99) state.gateScanPct + 1 else 99
                    state.copy(
                        tatSecondsElapsed = newTat,
                        tokenRefreshSec = newTokenSec,
                        auctionSecondsRemaining = newAuctionSec,
                        gateScanPct = newGatePct
                    )
                }
            }
        }
    }

    fun selectTab(tab: OperationalTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectActiveRunSubScreen(subScreen: ActiveRunSubScreen) {
        _uiState.update { it.copy(activeRunSubScreen = subScreen) }
    }

    fun selectYardBaysSubScreen(subScreen: YardBaysSubScreen) {
        _uiState.update { it.copy(yardBaysSubScreen = subScreen) }
    }

    fun selectControlTowerSubScreen(subScreen: ControlTowerSubScreen) {
        _uiState.update { it.copy(controlTowerSubScreen = subScreen) }
    }

    fun confirmGateEntry() {
        viewModelScope.launch {
            _toastEvent.emit("Gate 3 Cleared • Ingress to Lane 2 Activated")
            _uiState.update { it.copy(gateCleared = true, barrierAngle = 58) }
        }
    }

    fun toggleDockLockEngagement() {
        val current = _uiState.value.dockLockEngaged
        _uiState.update { it.copy(dockLockEngaged = !current) }
        viewModelScope.launch {
            _toastEvent.emit(if (!current) "Dock Lock Hook Engaged • Green Interlock Synced" else "Dock Lock Disengaged")
        }
    }

    fun selectPallet(pallet: PalletSkid) {
        _uiState.update { it.copy(selectedPallet = pallet) }
        viewModelScope.launch {
            _toastEvent.emit("Auditing Pallet #${pallet.id}: ${pallet.weightLbs} lbs • ${pallet.temperatureC}°C")
        }
    }

    fun addSignaturePoint(point: Offset?) {
        _uiState.update {
            it.copy(signaturePoints = it.signaturePoints + point)
        }
    }

    fun clearSignature() {
        _uiState.update { it.copy(signaturePoints = emptyList(), isFastPassBiometric = false) }
        viewModelScope.launch {
            _toastEvent.emit("Signature Pad Reset")
        }
    }

    fun toggleFastPassBiometric() {
        val current = _uiState.value.isFastPassBiometric
        _uiState.update { it.copy(isFastPassBiometric = !current) }
        viewModelScope.launch {
            _toastEvent.emit(if (!current) "Biometric Driver FastPass #DR-4482 Authenticated" else "Biometric Signoff Cleared")
        }
    }

    fun completePickupAndOutbound() {
        val state = _uiState.value
        val hasSigned = state.signaturePoints.isNotEmpty() || state.isFastPassBiometric
        if (!hasSigned) {
            viewModelScope.launch {
                _toastEvent.emit("Sign above or tap Instant Accept via Biometrics first")
            }
            return
        }

        _uiState.update { it.copy(isCustodySigned = true, outboundDispatched = true) }
        viewModelScope.launch {
            _toastEvent.emit("Pickup Completed: Cryptographic e-BOL SHA-256 Ledger Stamp Verified")
        }
    }

    fun approveProtocolAlphaReroute() {
        _uiState.update {
            it.copy(
                rerouteApproved = true,
                emergencyExecuted = true,
                revisedEtaUtc = "15:38"
            )
        }
        viewModelScope.launch {
            _toastEvent.emit("Protocol Alpha Approved: Loop 12 Bypass pushed to Garmin ELD (+14m recovered)")
        }
    }

    fun requestDockWindowHold() {
        _uiState.update { it.copy(dockHoldApproved = true) }
        viewModelScope.launch {
            _toastEvent.emit("Dallas DC #8492 Bay 08 Hold Granted until 16:00 UTC")
        }
    }

    fun triggerYardRelayBobtail() {
        _uiState.update { it.copy(relayMobilized = true) }
        viewModelScope.launch {
            _toastEvent.emit("Unit #4480 Mobilized from Sector 3 Yard to MM 138")
        }
    }

    fun toggleTorch() {
        _uiState.update { it.copy(isTorchOn = !it.isTorchOn) }
    }

    fun setExposure(value: Float) {
        _uiState.update { it.copy(exposureCompensation = value) }
    }

    fun transmitSealMacroPhoto() {
        viewModelScope.launch {
            _toastEvent.emit("Transmitting Macro OCR Photo... 99.4% Match Confirmed")
            delay(800)
            _uiState.update {
                it.copy(
                    isSealPhotoTransmitted = true,
                    isOverrideApproved = true,
                    gateCleared = true,
                    tatPaused = false
                )
            }
            _toastEvent.emit("Bolt Seal Parity Validated: Gate Barrier 03-L2 Lifting")
        }
    }

    fun selectWorkaround(code: String) {
        _uiState.update { it.copy(selectedWorkaround = code) }
    }

    fun cyclePneumaticChock() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPneumaticCycling = true) }
            _toastEvent.emit("Cycling Pneumatic Manifold (10s purge)...")
            delay(2000)
            _uiState.update {
                it.copy(
                    isPneumaticCycling = false,
                    isChockFaultActive = false,
                    dockLockEngaged = true
                )
            }
            _toastEvent.emit("Chock Sensor Contact Verified: Solid Green Interlock Active")
        }
    }

    fun selectBid(bidId: String) {
        _uiState.update { it.copy(selectedBidId = bidId) }
    }

    fun acceptSelectedBid() {
        val selected = _uiState.value.carrierBids.find { it.id == _uiState.value.selectedBidId } ?: return
        _uiState.update { it.copy(auctionBound = true) }
        viewModelScope.launch {
            _toastEvent.emit("Cascade Bound: ${selected.carrierName} (${selected.unitNumber}) Dispatched at $${selected.bidAmountUsd}")
        }
    }

    fun manualOverrideHoldWithDriver() {
        _uiState.update { it.copy(manualHoldWithMarcus = true) }
        viewModelScope.launch {
            _toastEvent.emit("Manual Override: Load held with Marcus Vance. Initiating expedited routing.")
        }
    }

    fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastEvent.emit(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
