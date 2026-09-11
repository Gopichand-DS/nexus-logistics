export type OperationalTab = 'INGRESS' | 'YARD_STOWAGE' | 'DOCS_EPOP' | 'CONTROL_TOWER';

export type FontFamilyChoice = 'calibri' | 'times';

export type ThemeChoice = 'light' | 'dark';

export interface PalletItem {
  id: number;
  lotNumber: string;
  weightLbs: number;
  temperatureC: number;
  status: 'STAGED' | 'LOADED' | 'VERIFIED';
  rfidTag: string;
}

export interface CarrierBid {
  id: string;
  carrierName: string;
  unitNumber: string;
  equipmentType: string;
  bidAmountUsd: number;
  distanceInfo: string;
  protocolNote: string;
  isLowest?: boolean;
}

export interface IngressState {
  orderNumber: string;
  assignedBay: string;
  carrierName: string;
  tractorPlate: string;
  manifestId: string;
  boltSealExpected: string;
  boltSealScanned: string;
  ocrConfidence: number;
  isHoldResolved: boolean;
}

export interface YardState {
  dockLockEngaged: boolean;
  isAirPurged: boolean;
  pallets: PalletItem[];
  selectedPalletId: number | null;
  reeferTempC: number;
}

export interface DocsState {
  isBiometricFastPass: boolean;
  outboundDispatched: boolean;
  signatureDataUrl: string | null;
  payoutBonusUsd: number;
  ledgerHash: string;
}

export interface ControlTowerState {
  incidentActive: boolean;
  rerouteApproved: boolean;
  dockHoldApproved: boolean;
  auctionSeconds: number;
  selectedBidId: string;
  bids: CarrierBid[];
}

export interface LogisticsContextType {
  tab: OperationalTab;
  setTab: (tab: OperationalTab) => void;
  font: FontFamilyChoice;
  setFont: (font: FontFamilyChoice) => void;
  theme: ThemeChoice;
  setTheme: (theme: ThemeChoice) => void;
  tatSeconds: number;
  ingress: IngressState;
  yard: YardState;
  docs: DocsState;
  controlTower: ControlTowerState;
  
  // Scalable action methods
  triggerMacroOcrScan: () => void;
  overrideYardMarshal: () => void;
  toggleDockLock: () => void;
  purgePneumatics: () => Promise<void>;
  selectPallet: (id: number) => void;
  toggleBiometric: () => void;
  dispatchOutbound: (signatureUrl?: string) => void;
  approveProtocolAlpha: () => void;
  requestDockHold: () => void;
  selectBid: (bidId: string) => void;
  acceptBid: () => void;
  triggerSos: () => void;
  
  // Notification system
  toastMessage: string | null;
  clearToast: () => void;
  showToast: (msg: string) => void;
}
