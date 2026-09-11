import React, { useState } from 'react';
import { Camera, QrCode, AlertTriangle, ShieldCheck, Sun, Eye, CheckCircle2, RotateCcw, Lock } from 'lucide-react';
import { LogisticsState } from '../types';

interface Props {
  state: LogisticsState;
  onUpdateState: (updates: Partial<LogisticsState>) => void;
  onNotify: (msg: string) => void;
}

export const IngressModule: React.FC<Props> = ({ state, onUpdateState, onNotify }) => {
  const [torchOn, setTorchOn] = useState(false);
  const [exposure, setExposure] = useState(1.2);
  const [scanning, setScanning] = useState(false);
  const [showOverrideModal, setShowOverrideModal] = useState(false);

  const handleScanMacro = () => {
    setScanning(true);
    setTimeout(() => {
      setScanning(false);
      onUpdateState({
        boltSealScanned: '#SL-884920',
        ocrConfidence: 99.4,
        isHoldResolved: true,
      });
      onNotify('High-Resolution Macro OCR Match Confirmed (99.4% Confidence). Barrier Arm Released.');
    }, 1800);
  };

  const handleManualOverride = () => {
    onUpdateState({
      isHoldResolved: true,
      boltSealScanned: '#SL-884920 (Marshal Override)',
      ocrConfidence: 100,
    });
    setShowOverrideModal(false);
    onNotify('Yard Marshal Sgt. Kowalski authorized physical seal override. Gate 04 arm raised.');
  };

  return (
    <div className="space-y-6">
      {/* Top Banner Status */}
      <div className={`p-4 rounded-xl border flex flex-wrap items-center justify-between gap-4 ${
        state.isHoldResolved
          ? 'bg-[#51E77B]/10 border-[#51E77B]/40 text-[#51E77B]'
          : 'bg-[#FF5449]/10 border-[#FF5449]/40 text-[#FF5449]'
      }`}>
        <div className="flex items-center gap-3">
          {state.isHoldResolved ? (
            <CheckCircle2 className="w-6 h-6 text-[#51E77B]" />
          ) : (
            <AlertTriangle className="w-6 h-6 text-[#FF5449] animate-pulse" />
          )}
          <div>
            <div className="font-bold tracking-wide text-sm">
              {state.isHoldResolved
                ? 'GATE INGRESS CLEARED: BARRIER ARM ELEVATED (58°)'
                : 'GATE 04 INGRESS EXCEPTION: BOLT SEAL DISCREPANCY HOLD'}
            </div>
            <div className="text-xs text-[#8E9BB5] mt-0.5">
              {state.isHoldResolved
                ? 'Driver Marcus Vance cleared for transit to assigned Bay 14'
                : 'Origin manifest seal #SL-884920 does not match low-res gate camera read #TK-99201'}
            </div>
          </div>
        </div>

        {!state.isHoldResolved && (
          <button
            onClick={() => setShowOverrideModal(true)}
            className="bg-[#FF5449]/20 hover:bg-[#FF5449]/30 text-white text-xs font-semibold px-4 py-2 rounded-lg border border-[#FF5449] transition cursor-pointer"
          >
            YARD MARSHAL OVERRIDE
          </button>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Contactless Optical Beacon */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#F59E0B] font-mono font-bold">STAGE 1: UPSTREAM BIND</div>
              <div className="text-lg font-bold text-white">Contactless Optical Beacon</div>
            </div>
            <span className="px-2.5 py-1 rounded bg-[#51E77B]/20 text-[#51E77B] text-[10px] font-mono font-semibold border border-[#51E77B]/40">
              TELEMATICS MATCHED
            </span>
          </div>

          <div className="flex flex-col sm:flex-row items-center gap-6 py-2">
            {/* Pulsing QR Code */}
            <div className="relative p-4 bg-white rounded-xl shadow-lg animate-beacon flex-shrink-0">
              <div className="w-36 h-36 flex flex-col items-center justify-center bg-black rounded-lg text-white font-mono text-center p-2">
                <QrCode className="w-24 h-24 text-[#F59E0B]" />
                <span className="text-[9px] text-[#8E9BB5]">NX-882-INBOUND</span>
              </div>
            </div>

            <div className="space-y-3 text-xs flex-1 w-full">
              <div className="flex justify-between border-b border-[#1C2B4E] pb-1.5">
                <span className="text-[#8E9BB5]">Tractor Cab ID:</span>
                <span className="font-mono text-white font-bold">{state.tractorPlate} (Tier-1)</span>
              </div>
              <div className="flex justify-between border-b border-[#1C2B4E] pb-1.5">
                <span className="text-[#8E9BB5]">Assigned Driver:</span>
                <span className="text-white font-bold">{state.carrierName}</span>
              </div>
              <div className="flex justify-between border-b border-[#1C2B4E] pb-1.5">
                <span className="text-[#8E9BB5]">Inbound Geofence:</span>
                <span className="text-[#51E77B] font-mono">Dallas North Gate 04 (0m)</span>
              </div>
              <div className="flex justify-between border-b border-[#1C2B4E] pb-1.5">
                <span className="text-[#8E9BB5]">Target Staging Bay:</span>
                <span className="font-mono font-bold text-[#F59E0B]">{state.assignedBay} (Ready)</span>
              </div>
            </div>
          </div>
        </div>

        {/* Macro OCR Seal Camera Viewfinder */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#4CD7F6] font-mono font-bold">STAGE 2: SEAL VERIFICATION</div>
              <div className="text-lg font-bold text-white">Mobile Macro OCR Viewfinder</div>
            </div>
            <span className="px-2.5 py-1 rounded bg-[#4CD7F6]/20 text-[#4CD7F6] text-[10px] font-mono font-semibold border border-[#4CD7F6]/40">
              {state.isHoldResolved ? 'MATCH: 99.4%' : 'HOLD REQUIRED'}
            </span>
          </div>

          {/* Viewfinder Canvas Simulation */}
          <div className="relative h-48 bg-[#090F1E] border border-[#2A3B66] rounded-xl overflow-hidden flex items-center justify-center">
            {/* Viewfinder Reticle */}
            <div className="absolute inset-4 border-2 border-dashed border-[#F59E0B]/50 rounded-lg pointer-events-none flex items-center justify-center">
              {scanning && (
                <div className="absolute w-full h-1 bg-gradient-to-r from-transparent via-[#FF5449] to-transparent shadow-[0_0_10px_#FF5449] animate-laser" />
              )}
            </div>

            <div className="text-center space-y-2 z-10">
              <div className="font-mono text-xs text-[#8E9BB5]">TARGET BOLT SEAL:</div>
              <div className="font-mono text-2xl font-black tracking-widest text-[#F59E0B] bg-[#162444]/80 px-4 py-1.5 rounded-lg border border-[#F59E0B]/40">
                {scanning ? 'ANALYZING HIGH-RES ENGRAVING...' : state.boltSealScanned}
              </div>
              <div className="text-[10px] text-[#4CD7F6] font-mono">
                EXPECTED MANIFEST: {state.boltSealExpected}
              </div>
            </div>

            {torchOn && (
              <div className="absolute inset-0 bg-[#F59E0B]/10 pointer-events-none" />
            )}
          </div>

          {/* Viewfinder Controls */}
          <div className="flex flex-wrap items-center justify-between gap-3 pt-1 text-xs">
            <div className="flex items-center gap-2">
              <button
                onClick={() => setTorchOn(!torchOn)}
                className={`p-2 rounded-lg border flex items-center gap-1.5 cursor-pointer ${
                  torchOn ? 'bg-[#F59E0B] text-black border-[#F59E0B]' : 'bg-[#162444] text-[#8E9BB5] border-[#2A3B66]'
                }`}
              >
                <Sun className="w-4 h-4" />
                <span>{torchOn ? 'TORCH ON' : 'TORCH OFF'}</span>
              </button>

              <div className="flex items-center gap-2 bg-[#162444] px-3 py-1.5 rounded-lg border border-[#2A3B66] text-[#8E9BB5] font-mono">
                <span>EV +{exposure.toFixed(1)}</span>
              </div>
            </div>

            <button
              onClick={handleScanMacro}
              disabled={scanning}
              className="bg-[#F59E0B] hover:bg-[#D97706] disabled:opacity-50 text-black font-bold px-5 py-2 rounded-lg flex items-center gap-2 shadow-md transition cursor-pointer"
            >
              <Camera className="w-4 h-4" />
              <span>{scanning ? 'SCANNING...' : 'TRIGGER MACRO OCR SCAN'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Marshal Override Modal */}
      {showOverrideModal && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50">
          <div className="bg-[#101B35] border border-[#FF5449] rounded-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center gap-3 text-[#FF5449]">
              <ShieldCheck className="w-7 h-7" />
              <div className="text-lg font-bold">Yard Marshal Physical Override</div>
            </div>
            <div className="text-xs text-[#E0E6F8] space-y-2">
              <p>
                Officer: <strong className="text-white">Sgt. D. Kowalski (#YM-08)</strong>
              </p>
              <p className="text-[#8E9BB5]">
                Physical inspection of Bolt Seal #SL-884920 completed. High-tensile steel pin intact with no signs of tampering. Origin tamper-evident barcode matches Dallas North shipment manifest.
              </p>
            </div>
            <div className="flex justify-end gap-3 pt-3 border-t border-[#1C2B4E]">
              <button
                onClick={() => setShowOverrideModal(false)}
                className="px-4 py-2 rounded-lg bg-[#162444] text-[#8E9BB5] text-xs hover:text-white cursor-pointer"
              >
                CANCEL
              </button>
              <button
                onClick={handleManualOverride}
                className="px-4 py-2 rounded-lg bg-[#51E77B] text-black font-bold text-xs hover:bg-[#43C769] cursor-pointer"
              >
                CONFIRM OVERRIDE & ELEVATE ARM
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
