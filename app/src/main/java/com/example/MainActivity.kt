package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OperationalTab
import com.example.ui.LogisticsUiState
import com.example.ui.LogisticsViewModel
import com.example.ui.components.BottomOperationalNavigationBar
import com.example.ui.components.TopOperationalHud
import com.example.ui.screens.*
import com.example.ui.theme.NexusLogisticsTheme
import com.example.ui.theme.TerminalSurface

class MainActivity : ComponentActivity() {

    private val viewModel: LogisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NexusLogisticsTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }
                var showSosDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    viewModel.toastEvent.collect { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopOperationalHud(
                            uiState = uiState,
                            onEmergencySosClick = { showSosDialog = true }
                        )
                    },
                    bottomBar = {
                        BottomOperationalNavigationBar(
                            activeTab = uiState.activeTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(TerminalSurface)
                    ) {
                        when (uiState.activeTab) {
                            OperationalTab.ACTIVE_RUN -> {
                                ActiveRunContainerScreen(
                                    uiState = uiState,
                                    onSubScreenSelect = { viewModel.selectActiveRunSubScreen(it) },
                                    onConfirmGateEntry = { viewModel.confirmGateEntry() },
                                    onToggleTorch = { viewModel.toggleTorch() },
                                    onSetExposure = { viewModel.setExposure(it) },
                                    onTransmitMacroPhoto = { viewModel.transmitSealMacroPhoto() },
                                    onEmitToast = { viewModel.emitToast(it) }
                                )
                            }
                            OperationalTab.YARD_BAYS -> {
                                YardBaysContainerScreen(
                                    uiState = uiState,
                                    onSubScreenSelect = { viewModel.selectYardBaysSubScreen(it) },
                                    onToggleDockLock = { viewModel.toggleDockLockEngagement() },
                                    onSelectPallet = { viewModel.selectPallet(it) },
                                    onSelectWorkaround = { viewModel.selectWorkaround(it) },
                                    onCyclePneumatics = { viewModel.cyclePneumaticChock() },
                                    onEmitToast = { viewModel.emitToast(it) }
                                )
                            }
                            OperationalTab.DOCS_EPOP -> {
                                DocsEpopContainerScreen(
                                    uiState = uiState,
                                    onAddSignaturePoint = { viewModel.addSignaturePoint(it) },
                                    onClearSignature = { viewModel.clearSignature() },
                                    onToggleBiometric = { viewModel.toggleFastPassBiometric() },
                                    onCompletePickup = { viewModel.completePickupAndOutbound() },
                                    onEmitToast = { viewModel.emitToast(it) }
                                )
                            }
                            OperationalTab.CONTROL_TOWER -> {
                                ControlTowerContainerScreen(
                                    uiState = uiState,
                                    onSubScreenSelect = { viewModel.selectControlTowerSubScreen(it) },
                                    onApproveReroute = { viewModel.approveProtocolAlphaReroute() },
                                    onRequestDockHold = { viewModel.requestDockWindowHold() },
                                    onTriggerRelay = { viewModel.triggerYardRelayBobtail() },
                                    onSelectBid = { viewModel.selectBid(it) },
                                    onAcceptBid = { viewModel.acceptSelectedBid() },
                                    onManualHoldWithDriver = { viewModel.manualOverrideHoldWithDriver() },
                                    onEmitToast = { viewModel.emitToast(it) }
                                )
                            }
                        }

                        if (showSosDialog) {
                            AlertDialog(
                                onDismissRequest = { showSosDialog = false },
                                title = { Text("OPERATIONAL SOS EMERGENCY") },
                                text = {
                                    Text("Broadcast emergency telematic alert to Dallas North Dispatch, Yard Safety Marshall Kowalski, and TX Highway Patrol?")
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            viewModel.emitToast("EMERGENCY SIGNAL BROADCAST TO DISPATCH & YARD MARSHAL")
                                            showSosDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("TRANSMIT SOS")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showSosDialog = false }) {
                                        Text("CANCEL")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
