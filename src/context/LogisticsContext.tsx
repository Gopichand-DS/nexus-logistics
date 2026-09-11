import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  OperationalTab,
  FontFamilyChoice,
  ThemeChoice,
  LogisticsContextType,
  PalletItem,
  CarrierBid,
  IngressState,
  YardState,
  DocsState,
  ControlTowerState,
} from '../types';

const INITIAL_PALLETS: PalletItem[] = Array.from({ length: 24 }).map((_, idx) => ({
  id: idx + 1,
  lotNumber: `LOT-2026-X${8820 + idx}`,
  weightLbs: 1740 + ((idx * 31) % 160),
  temperatureC: -20.2 + (idx % 4) * 0.1,
  status: idx < 18 ? 'LOADED' : 'STAGED',
  rfidTag: `RFID-DFW-00${(idx + 1).toString().padStart(2, '0')}`,
}));

const INITIAL_BIDS: CarrierBid[] = [
  {
    id: 'bid-1',
    carrierName: 'Apex Logistics Direct',
    unitNumber: 'Cab #771',
    equipmentType: '53ft Reefer Drop & Hook',
    bidAmountUsd: 420,
    distanceInfo: '12m away (MM 142)',
    protocolNote: 'Pre-vetted Tier-1 cold-chain fleet with live CAN-bus telematics.',
    isLowest: true,
  },
  {
    id: 'bid-2',
    carrierName: 'Lone Star Freight Relay',
    unitNumber: 'Cab #550',
    equipmentType: '53ft Reefer Solo',
    bidAmountUsd: 455,
    distanceInfo: '18m away (Sector 3)',
    protocolNote: 'Standby tractor available for fast trailer coupling.',
  },
  {
    id: 'bid-3',
    carrierName: 'SwiftLine Intermodal',
    unitNumber: 'Cab #109',
    equipmentType: '53ft Multi-temp Reefer',
    bidAmountUsd: 490,
    distanceInfo: '24m away (I-30E)',
    protocolNote: 'Guaranteed 20m response window with demurrage risk indemnity.',
  },
];

const LogisticsContext = createContext<LogisticsContextType | undefined>(undefined);

export const LogisticsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Navigation & UI preferences
  const [tab, setTab] = useState<OperationalTab>('INGRESS');
  const [font, setFont] = useState<FontFamilyChoice>('calibri');
  const [theme, setTheme] = useState<ThemeChoice>('light');
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Turnaround Time (TAT) Clock and Bidding Countdown
  const [tatSeconds, setTatSeconds] = useState(412); // 6m 52s
  const [auctionSeconds, setAuctionSeconds] = useState(210); // 3m 30s

  // State Domains
  const [ingress, setIngress] = useState<IngressState>({
    orderNumber: 'NX-88204-DFW',
    assignedBay: 'BAY 14 (NORTH)',
    carrierName: 'Marcus Vance',
    tractorPlate: 'TX-4412-HV',
    manifestId: 'BOL-DAL-9921',
    boltSealExpected: '#SL-884920',
    boltSealScanned: '#TK-99201 (Mismatch)',
    ocrConfidence: 62.4,
    isHoldResolved: false,
  });

  const [yard, setYard] = useState<YardState>({
    dockLockEngaged: false,
    isAirPurged: false,
    pallets: INITIAL_PALLETS,
    selectedPalletId: 1,
    reeferTempC: -20.2,
  });

  const [docs, setDocs] = useState<DocsState>({
    isBiometricFastPass: false,
    outboundDispatched: false,
    signatureDataUrl: null,
    payoutBonusUsd: 45.0,
    ledgerHash: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
  });

  const [controlTower, setControlTower] = useState<ControlTowerState>({
    incidentActive: true,
    rerouteApproved: false,
    dockHoldApproved: false,
    auctionSeconds: 210,
    selectedBidId: 'bid-1',
    bids: INITIAL_BIDS,
  });

  // Sync theme attribute to <html> element
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  // Real-time ticking timers
  useEffect(() => {
    const interval = setInterval(() => {
      setTatSeconds((prev) => prev + 1);
      setAuctionSeconds((prev) => (prev > 0 ? prev - 1 : 0));
      setControlTower((prev) => ({
        ...prev,
        auctionSeconds: prev.auctionSeconds > 0 ? prev.auctionSeconds - 1 : 0,
      }));
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  // Notifications
  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4500);
  };

  const clearToast = () => setToastMessage(null);

  // Ingress Actions
  const triggerMacroOcrScan = () => {
    showToast('Executing Macro OCR high-res scan...');
    setTimeout(() => {
      setIngress((prev) => ({
        ...prev,
        boltSealScanned: '#SL-884920',
        ocrConfidence: 99.6,
        isHoldResolved: true,
      }));
      showToast('Macro OCR Verified: Seal #SL-884920 matches manifest. Barrier arm elevated.');
    }, 1200);
  };

  const overrideYardMarshal = () => {
    setIngress((prev) => ({
      ...prev,
      boltSealScanned: '#SL-884920 (Marshal Override)',
      ocrConfidence: 100.0,
      isHoldResolved: true,
    }));
    showToast('Sgt. Kowalski authorized physical seal verification. Gate arm raised.');
  };

  // Yard Actions
  const toggleDockLock = () => {
    setYard((prev) => {
      const next = !prev.dockLockEngaged;
      showToast(next ? 'Hydraulic RIG Dock Lock Engaged. Green Light Activated.' : 'Dock Lock Disengaged. Red Light Activated.');
      return { ...prev, dockLockEngaged: next };
    });
  };

  const purgePneumatics = async () => {
    showToast('Purging air manifold and recycling ultrasonic chocks...');
    await new Promise((res) => setTimeout(res, 1800));
    setYard((prev) => ({ ...prev, isAirPurged: true }));
    showToast('Pneumatic chock pressure equalized. Zero-gap seating confirmed.');
  };

  const selectPallet = (id: number) => {
    setYard((prev) => ({ ...prev, selectedPalletId: id }));
  };

  // Docs Actions
  const toggleBiometric = () => {
    setDocs((prev) => {
      const next = !prev.isBiometricFastPass;
      showToast(next ? 'Biometric FastPass enabled for Driver Marcus Vance.' : 'Switched to Stylus Signature input.');
      return { ...prev, isBiometricFastPass: next };
    });
  };

  const dispatchOutbound = (signatureUrl?: string) => {
    setDocs((prev) => ({
      ...prev,
      outboundDispatched: true,
      signatureDataUrl: signatureUrl || prev.signatureDataUrl,
    }));
    showToast('Outbound Dispatch Complete! Cryptographic e-BOL published to shipper ledger.');
  };

  // Control Tower Actions
  const approveProtocolAlpha = () => {
    setControlTower((prev) => ({ ...prev, rerouteApproved: true }));
    showToast('Protocol Alpha Approved: Loop 12 South bypass sent to Cab Navigation (+14m recovered).');
  };

  const requestDockHold = () => {
    setControlTower((prev) => ({ ...prev, dockHoldApproved: true }));
    showToast('Protocol Bravo Confirmed: Dallas DC Bay 08 reservation held until 16:00 UTC.');
  };

  const selectBid = (bidId: string) => {
    setControlTower((prev) => ({ ...prev, selectedBidId: bidId }));
  };

  const acceptBid = () => {
    const selected = controlTower.bids.find((b) => b.id === controlTower.selectedBidId);
    showToast(`Cascade auction awarded to ${selected?.carrierName || 'Carrier'} ($${selected?.bidAmountUsd}). Unit dispatched.`);
  };

  const triggerSos = () => {
    showToast('EMERGENCY BROADCAST TRANSMITTED: Yard Marshal & Texas Highway Patrol notified.');
  };

  return (
    <LogisticsContext.Provider
      value={{
        tab,
        setTab,
        font,
        setFont,
        theme,
        setTheme,
        tatSeconds,
        ingress,
        yard,
        docs,
        controlTower,
        triggerMacroOcrScan,
        overrideYardMarshal,
        toggleDockLock,
        purgePneumatics,
        selectPallet,
        toggleBiometric,
        dispatchOutbound,
        approveProtocolAlpha,
        requestDockHold,
        selectBid,
        acceptBid,
        triggerSos,
        toastMessage,
        clearToast,
        showToast,
      }}
    >
      {children}
    </LogisticsContext.Provider>
  );
};

export const useLogistics = (): LogisticsContextType => {
  const context = useContext(LogisticsContext);
  if (!context) {
    throw new Error('useLogistics must be used within a LogisticsProvider');
  }
  return context;
};
