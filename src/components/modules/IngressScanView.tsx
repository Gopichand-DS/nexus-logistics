import React, { useState } from 'react';
import { Camera, QrCode, AlertTriangle, ShieldCheck, Sun, CheckCircle2, RotateCcw, Lock, ExternalLink } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';

export const IngressScanView: React.FC = () => {
  const { ingress, font, triggerMacroOcrScan, overrideYardMarshal } = useLogistics();
  const [torchOn, setTorchOn] = useState(false);
  const [exposure, setExposure] = useState(1.2);
  const [isScanning, setIsScanning] = useState(false);
  const [showOverrideModal, setShowOverrideModal] = useState(false);

  const handleScan = () => {
    setIsScanning(true);
    setTimeout(() => {
      setIsScanning(false);
      triggerMacroOcrScan();
    }, 1500);
  };

  const handleConfirmOverride = () => {
    overrideYardMarshal();
    setShowOverrideModal(false);
  };

  const isTimes = font === 'times';

  return (
    <div className={`space-y-6 ${isTimes ? 'font-times' : 'font-calibri'}`}>
      {/* Top Banner Status */}
      <div className={`p-4 rounded-xl border flex flex-wrap items-center justify-between gap-4 transition ${
        ingress.isHoldResolved
          ? 'bg-emerald-50 dark:bg-emerald-950/30 border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300'
          : 'bg-red-50 dark:bg-red-950/30 border-red-200 dark:border-red-800 text-red-800 dark:text-red-300'
      }`}>
        <div className="flex items-center gap-3">
          {ingress.isHoldResolved ? (
            <CheckCircle2 className="w-6 h-6 text-emerald-600 dark:text-emerald-400 shrink-0" />
          ) : (
            <AlertTriangle className="w-6 h-6 text-red-600 dark:text-red-400 animate-pulse shrink-0" />
          )}
          <div>
            <div className={`font-bold text-sm tracking-wide ${isTimes ? 'text-base' : ''}`}>
              {ingress.isHoldResolved
                ? 'GATE INGRESS AUTHORIZED: BARRIER ARM ELEVATED (58°)'
                : 'GATE 04 INGRESS EXCEPTION: BOLT SEAL DISCREPANCY HOLD'}
            </div>
            <div className="text-xs text-[var(--text-muted)] mt-0.5 font-normal">
              {ingress.isHoldResolved
                ? 'Driver Marcus Vance cleared for staging transit to Bay 14 North.'
                : 'Origin manifest seal #SL-884920 does not match low-res gate camera read #TK-99201.'}
            </div>
          </div>
        </div>

        {!ingress.isHoldResolved && (
          <button
            onClick={() => setShowOverrideModal(true)}
            className="bg-red-600 hover:bg-red-700 text-white text-xs font-bold px-4 py-2 rounded-lg shadow-xs transition cursor-pointer"
          >
            YARD MARSHAL OVERRIDE
          </button>
        )}
      </div>

      {/* Main 2-Column Responsive Card Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Module 1: Upstream Telematics & Optical Beacon */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
                Phase 1: Upstream Bind
              </div>
              <h2 className="text-lg font-bold text-[var(--text-main)]">
                Contactless Optical Beacon
              </h2>
            </div>
            <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300 text-[11px] font-mono-data font-bold">
              CAN-BUS LINKED
            </span>
          </div>

          <div className="flex flex-col sm:flex-row items-center gap-6 py-2">
            {/* Animated QR Code Emblem */}
            <div className="relative p-3 bg-white rounded-xl shadow-md border border-[var(--border-card)] shrink-0 animate-pulse-ring">
              <div className="w-32 h-32 flex flex-col items-center justify-center bg-slate-900 rounded-lg text-white font-mono-data p-2">
                <QrCode className="w-20 h-20 text-amber-400" />
                <span className="text-[9px] text-slate-400 tracking-wider">NX-DFW-882</span>
              </div>
            </div>

            {/* Ingress Details Table */}
            <div className="space-y-2.5 text-xs flex-1 w-full">
              <div className="flex justify-between border-b border-[var(--border-card)] pb-1.5">
                <span className="text-[var(--text-muted)]">Order Manifest:</span>
                <span className="font-mono-data text-[var(--text-main)] font-bold">{ingress.orderNumber}</span>
              </div>
              <div className="flex justify-between border-b border-[var(--border-card)] pb-1.5">
                <span className="text-[var(--text-muted)]">Tractor ID:</span>
                <span className="font-mono-data text-[var(--text-main)] font-bold">{ingress.tractorPlate} (Tier-1)</span>
              </div>
              <div className="flex justify-between border-b border-[var(--border-card)] pb-1.5">
                <span className="text-[var(--text-muted)]">Authorized Driver:</span>
                <span className="text-[var(--text-main)] font-bold">{ingress.carrierName}</span>
              </div>
              <div className="flex justify-between border-b border-[var(--border-card)] pb-1.5">
                <span className="text-[var(--text-muted)]">Geofence Proximity:</span>
                <span className="text-emerald-600 dark:text-emerald-400 font-mono-data font-bold">Inside Gate 04 (0m)</span>
              </div>
              <div className="flex justify-between">
                <span className="text-[var(--text-muted)]">Target Staging Bay:</span>
                <span className="font-mono-data font-bold text-amber-600 dark:text-amber-400">{ingress.assignedBay}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Module 2: High-Resolution Macro OCR Viewfinder */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-sky-600 dark:text-sky-400 font-mono-data font-bold uppercase tracking-wider">
                Phase 2: Tamper Verification
              </div>
              <h2 className="text-lg font-bold text-[var(--text-main)]">
                Mobile Macro OCR Viewfinder
              </h2>
            </div>
            <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-mono-data font-bold ${
              ingress.isHoldResolved
                ? 'bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300'
                : 'bg-amber-100 dark:bg-amber-900/40 text-amber-800 dark:text-amber-300'
            }`}>
              {ingress.isHoldResolved ? `MATCH: ${ingress.ocrConfidence}%` : 'HOLD REQUIRED'}
            </span>
          </div>

          {/* Viewfinder Display Box */}
          <div className="relative h-44 bg-slate-950 rounded-xl overflow-hidden flex items-center justify-center border border-[var(--border-card)]">
            {/* Viewfinder Frame */}
            <div className="absolute inset-4 border-2 border-dashed border-amber-500/50 rounded-lg pointer-events-none flex items-center justify-center">
              {isScanning && (
                <div className="absolute w-full h-1 bg-gradient-to-r from-transparent via-red-500 to-transparent shadow-lg animate-scan" />
              )}
            </div>

            <div className="text-center space-y-1.5 z-10 p-2">
              <div className="text-[11px] font-mono-data text-slate-400 uppercase">
                SCANNED HIGH-TENSILE SEAL:
              </div>
              <div className="font-mono-data text-xl sm:text-2xl font-black tracking-widest text-amber-400 bg-slate-900/80 px-4 py-1.5 rounded-lg border border-amber-500/40">
                {isScanning ? 'RUNNING HIGH-RES OCR...' : ingress.boltSealScanned}
              </div>
              <div className="text-[11px] text-sky-400 font-mono-data">
                MANIFEST EXPECTED: {ingress.boltSealExpected}
              </div>
            </div>

            {torchOn && (
              <div className="absolute inset-0 bg-amber-400/15 pointer-events-none" />
            )}
          </div>

          {/* Camera Controls Bar */}
          <div className="flex flex-wrap items-center justify-between gap-3 pt-1 text-xs">
            <div className="flex items-center gap-2">
              <button
                onClick={() => setTorchOn(!torchOn)}
                className={`px-3 py-1.5 rounded-lg border text-xs font-semibold flex items-center gap-1.5 transition cursor-pointer ${
                  torchOn
                    ? 'bg-amber-500 text-white border-amber-500'
                    : 'bg-[var(--bg-subtle)] border-[var(--border-card)] text-[var(--text-muted)] hover:text-[var(--text-main)]'
                }`}
              >
                <Sun className="w-3.5 h-3.5" />
                <span>{torchOn ? 'TORCH ON' : 'TORCH OFF'}</span>
              </button>

              <div className="px-3 py-1.5 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] text-[var(--text-muted)] font-mono-data text-xs">
                EV +{exposure.toFixed(1)}
              </div>
            </div>

            <button
              onClick={handleScan}
              disabled={isScanning}
              className="bg-amber-600 hover:bg-amber-700 disabled:opacity-50 text-white font-bold px-4 py-2 rounded-lg flex items-center gap-2 shadow-xs transition cursor-pointer text-xs"
            >
              <Camera className="w-4 h-4" />
              <span>{isScanning ? 'ANALYZING...' : 'TRIGGER MACRO OCR'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Yard Marshal Override Modal */}
      {showOverrideModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-[var(--bg-card)] border border-red-300 dark:border-red-900 rounded-xl max-w-md w-full p-6 space-y-4 shadow-xl">
            <div className="flex items-center gap-3 text-red-600 dark:text-red-400">
              <ShieldCheck className="w-7 h-7 shrink-0" />
              <div className="text-lg font-bold">Yard Marshal Physical Seal Override</div>
            </div>
            <div className="text-xs text-[var(--text-main)] space-y-2 leading-relaxed">
              <p>
                Officer in Charge: <strong className="font-bold">Sgt. D. Kowalski (Badge #YM-08)</strong>
              </p>
              <p className="text-[var(--text-muted)]">
                Physical inspection of Bolt Seal #SL-884920 completed. High-tensile steel pin verified intact with no tampering marks. Tamper-evident barcode conforms to Dallas North outbound shipping order.
              </p>
            </div>
            <div className="flex justify-end gap-3 pt-3 border-t border-[var(--border-card)]">
              <button
                onClick={() => setShowOverrideModal(false)}
                className="px-4 py-2 rounded-lg bg-[var(--bg-subtle)] text-[var(--text-muted)] hover:text-[var(--text-main)] text-xs font-semibold cursor-pointer"
              >
                CANCEL
              </button>
              <button
                onClick={handleConfirmOverride}
                className="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-xs cursor-pointer"
              >
                CONFIRM & RAISE BARRIER ARM
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
