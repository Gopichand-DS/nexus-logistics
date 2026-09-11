import React, { useState, useEffect } from 'react';
import { HeaderHud } from './components/HeaderHud';
import { IngressModule } from './components/IngressModule';
import { YardDockModule } from './components/YardDockModule';
import { DocsEpopModule } from './components/DocsEpopModule';
import { ControlTowerModule } from './components/ControlTowerModule';
import { LogisticsState, OperationalTab, PalletSkid, CarrierBid } from './types';
import { ShieldAlert, X } from 'lucide-react';

const INITIAL_PALLETS: PalletSkid[] = Array.from({ length: 24 }).map((_, i) => ({
  id: i + 1,
  lotNumber: `LOT-2026-X${(8800 + i).toString()}`,
  weightLbs: 1740 + ((i * 37) % 180),
  temperatureC: -20.4 + (i % 3) * 0.2,
  status: i < 18 ? 'LOADED' : 'STAGED',
  rfidTag: `RFID-90${i.toString().padStart(2, '0')}`,
}));

const INITIAL_BIDS: CarrierBid[] = [
  {
    id: 'bid-1',
    carrierName: 'Apex Logistics Direct',
    unitNumber: 'Tractor #771',
    equipmentType: '53ft Reefer Drop & Hook',
    bidAmountUsd: 420,
    distanceInfo: '12m away (MM 142)',
    protocolNote: 'Pre-qualified Tier-1 cold chain carrier with telematics live.',
    isLowest: true,
  },
  {
    id: 'bid-2',
    carrierName: 'Lone Star Freight Relay',
    unitNumber: 'Tractor #550',
    equipmentType: '53ft Reefer Solo',
    bidAmountUsd: 460,
    distanceInfo: '18m away (Sector 3)',
    protocolNote: 'Standby bobtail available for prompt yard trailer swap.',
  },
  {
    id: 'bid-3',
    carrierName: 'SwiftLine Intermodal',
    unitNumber: 'Tractor #109',
    equipmentType: '53ft Dry/Reefer Multi-temp',
    bidAmountUsd: 495,
    distanceInfo: '24m away (I-30E)',
    protocolNote: 'Guaranteed 20m response window with demurrage indemnity.',
  },
];

export const App: React.FC = () => {
  const [state, setState] = useState<LogisticsState>({
    activeTab: 'ACTIVE_RUN',
    orderNumber: 'NX-88204-DFW',
    assignedBay: 'DOCK BAY 14',
    carrierName: 'Marcus Vance',
    tractorPlate: 'TX-4412-HV',
    manifestId: 'BOL-DAL-9921',
    boltSealExpected: '#SL-884920',
    boltSealScanned: '#TK-99201 (Mismatch)',
    ocrConfidence: 61.2,
    isHoldResolved: false,
    dockLockEngaged: false,
    tatSeconds: 405, // 6m 45s
    auctionSeconds: 222, // 3m 42s
    rerouteApproved: false,
    dockHoldApproved: false,
    isBiometricFastPass: false,
    outboundDispatched: false,
    pallets: INITIAL_PALLETS,
    selectedPallet: INITIAL_PALLETS[0],
    selectedBidId: 'bid-1',
    bids: INITIAL_BIDS,
  });

  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [showSosModal, setShowSosModal] = useState(false);

  // Turnaround Time & Auction Timers
  useEffect(() => {
    const timer = setInterval(() => {
      setState((prev) => ({
        ...prev,
        tatSeconds: prev.tatSeconds + 1,
        auctionSeconds: prev.auctionSeconds > 0 ? prev.auctionSeconds - 1 : 0,
      }));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const notify = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4500);
  };

  const updateState = (updates: Partial<LogisticsState>) => {
    setState((prev) => ({ ...prev, ...updates }));
  };

  return (
    <div className="min-h-screen bg-[#0B1326] text-[#E0E6F8] flex flex-col font-sans selection:bg-[#F59E0B] selection:text-black">
      {/* Top Header HUD */}
      <HeaderHud
        state={state}
        onTabChange={(tab: OperationalTab) => updateState({ activeTab: tab })}
        onSosClick={() => setShowSosModal(true)}
      />

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6">
        {state.activeTab === 'ACTIVE_RUN' && (
          <IngressModule state={state} onUpdateState={updateState} onNotify={notify} />
        )}
        {state.activeTab === 'YARD_BAYS' && (
          <YardDockModule state={state} onUpdateState={updateState} onNotify={notify} />
        )}
        {state.activeTab === 'DOCS_EPOP' && (
          <DocsEpopModule state={state} onUpdateState={updateState} onNotify={notify} />
        )}
        {state.activeTab === 'CONTROL_TOWER' && (
          <ControlTowerModule state={state} onUpdateState={updateState} onNotify={notify} />
        )}
      </main>

      {/* Toast Notification Banner */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-50 max-w-md bg-[#162444] border border-[#F59E0B] rounded-xl p-4 shadow-[0_10px_30px_rgba(0,0,0,0.8)] flex items-start justify-between gap-3 text-xs animate-bounce">
          <div className="text-white font-medium">{toastMessage}</div>
          <button
            onClick={() => setToastMessage(null)}
            className="text-[#8E9BB5] hover:text-white cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Emergency SOS Modal */}
      {showSosModal && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center p-4 z-50">
          <div className="bg-[#101B35] border border-[#FF5449] rounded-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center gap-3 text-[#FF5449]">
              <ShieldAlert className="w-8 h-8 animate-pulse" />
              <div className="text-lg font-bold">OPERATIONAL SOS BROADCAST</div>
            </div>
            <div className="text-xs text-[#E0E6F8] space-y-2">
              <p>
                Initiate high-priority emergency telematic broadcast to Dallas North Yard Safety, Sgt. Kowalski, and Texas Highway Patrol?
              </p>
              <p className="text-[#8E9BB5] font-mono">
                Current Location: Gate 04 Staging Ingress (32.7767° N, 96.7970° W)
              </p>
            </div>
            <div className="flex justify-end gap-3 pt-3 border-t border-[#1C2B4E]">
              <button
                onClick={() => setShowSosModal(false)}
                className="px-4 py-2 rounded-lg bg-[#162444] text-[#8E9BB5] text-xs hover:text-white cursor-pointer"
              >
                CANCEL
              </button>
              <button
                onClick={() => {
                  notify('EMERGENCY SIGNAL BROADCAST TO DISPATCH & YARD SAFETY');
                  setShowSosModal(false);
                }}
                className="px-4 py-2 rounded-lg bg-[#FF5449] hover:bg-[#E03E34] text-white font-bold text-xs cursor-pointer"
              >
                BROADCAST SOS
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Footer status */}
      <footer className="border-t border-[#1C2B4E] bg-[#090F1E] py-3 text-center text-xs text-[#8E9BB5]">
        <div className="max-w-7xl mx-auto px-4 flex flex-wrap justify-between items-center gap-2">
          <span>Nexus Logistics First-Mile Execution • SLA OTP Target: ≥ 98.5%</span>
          <span className="font-mono text-[#51E77B]">Vercel Deployment Ready • Production Build v1.0</span>
        </div>
      </footer>
    </div>
  );
};
