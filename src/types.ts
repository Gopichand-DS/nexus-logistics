export type OperationalTab = 'ACTIVE_RUN' | 'YARD_BAYS' | 'DOCS_EPOP' | 'CONTROL_TOWER';

export interface PalletSkid {
  id: number;
  lotNumber: string;
  weightLbs: number;
  temperatureC: number;
  status: 'STAGED' | 'LOADED' | 'IN_TRANSIT' | 'OFFLOADED';
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

export interface LogisticsState {
  activeTab: OperationalTab;
  orderNumber: string;
  assignedBay: string;
  carrierName: string;
  tractorPlate: string;
  manifestId: string;
  boltSealExpected: string;
  boltSealScanned: string;
  ocrConfidence: number;
  isHoldResolved: boolean;
  dockLockEngaged: boolean;
  tatSeconds: number;
  auctionSeconds: number;
  rerouteApproved: boolean;
  dockHoldApproved: boolean;
  isBiometricFastPass: boolean;
  outboundDispatched: boolean;
  pallets: PalletSkid[];
  selectedPallet: PalletSkid | null;
  selectedBidId: string;
  bids: CarrierBid[];
}
