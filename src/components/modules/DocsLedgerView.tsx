import React, { useRef, useState, useEffect } from 'react';
import { FileText, Award, Fingerprint, Download, CheckCircle2, Shield, Hash, X } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';

export const DocsLedgerView: React.FC = () => {
  const { docs, ingress, font, toggleBiometric, dispatchOutbound } = useLogistics();
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [hasSignature, setHasSignature] = useState(false);
  const [showBolModal, setShowBolModal] = useState(false);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.strokeStyle = '#D97706';
    ctx.lineWidth = 2.5;
    ctx.lineCap = 'round';
  }, [docs.isBiometricFastPass]);

  const startDraw = (e: React.MouseEvent<HTMLCanvasElement> | React.TouchEvent<HTMLCanvasElement>) => {
    setIsDrawing(true);
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    const rect = canvas.getBoundingClientRect();
    const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
    const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY;
    ctx.beginPath();
    ctx.moveTo(clientX - rect.left, clientY - rect.top);
  };

  const draw = (e: React.MouseEvent<HTMLCanvasElement> | React.TouchEvent<HTMLCanvasElement>) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    const rect = canvas.getBoundingClientRect();
    const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
    const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY;
    ctx.lineTo(clientX - rect.left, clientY - rect.top);
    ctx.stroke();
    setHasSignature(true);
  };

  const stopDraw = () => {
    setIsDrawing(false);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    setHasSignature(false);
  };

  const handleCompletePickup = () => {
    const canvas = canvasRef.current;
    const dataUrl = canvas ? canvas.toDataURL() : undefined;
    dispatchOutbound(dataUrl);
  };

  const isTimes = font === 'times';

  return (
    <div className={`space-y-6 ${isTimes ? 'font-times' : 'font-calibri'}`}>
      {/* SLA Bonus Highlight Card */}
      <div className="p-5 bg-gradient-to-r from-emerald-50 to-emerald-100/40 dark:from-emerald-950/40 dark:to-emerald-900/20 border border-emerald-300 dark:border-emerald-800 rounded-xl flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-600 text-white flex items-center justify-center shrink-0 shadow-sm">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <span className="px-2.5 py-0.5 rounded-full bg-emerald-200 dark:bg-emerald-900/60 text-emerald-900 dark:text-emerald-200 text-[11px] font-mono-data font-bold">
              ON-TIME SLA BONUS QUALIFIED
            </span>
            <div className="text-xl font-black text-[var(--text-main)] mt-0.5">
              +${docs.payoutBonusUsd.toFixed(2)} CARRIER CREDIT AWARDED
            </div>
            <div className="text-xs text-[var(--text-muted)]">
              Turnaround Time: 19m 40s (5m 20s faster than target ≤25m threshold)
            </div>
          </div>
        </div>

        <button
          onClick={() => setShowBolModal(true)}
          className="flex items-center gap-2 bg-[var(--bg-card)] hover:bg-[var(--bg-subtle)] text-amber-600 dark:text-amber-400 border border-amber-500/50 px-4 py-2 rounded-lg text-xs font-bold transition shadow-xs cursor-pointer"
        >
          <FileText className="w-4 h-4" />
          <span>VIEW CRYPTOGRAPHIC e-BOL</span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chain of Custody Card */}
        <div className="clean-card p-5 space-y-4">
          <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
            Chain of Custody Transfer
          </div>

          <div className="space-y-2.5 text-xs">
            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
              <span className="text-[var(--text-muted)]">Order Manifest:</span>
              <span className="font-mono-data font-bold text-[var(--text-main)]">{ingress.orderNumber}</span>
            </div>
            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
              <span className="text-[var(--text-muted)]">Shipper Staging Marshal:</span>
              <span className="font-bold text-[var(--text-main)]">Sgt. D. Kowalski (#YM-08) - Certified</span>
            </div>
            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
              <span className="text-[var(--text-muted)]">Bolt Seal Stamp:</span>
              <span className="font-mono-data font-bold text-emerald-600 dark:text-emerald-400">
                {ingress.boltSealExpected} (Verified)
              </span>
            </div>
            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
              <span className="text-[var(--text-muted)]">Cold Chain Reefer Lock:</span>
              <span className="font-mono-data font-bold text-sky-600 dark:text-sky-400">
                -20.2°C Continuous Sensor
              </span>
            </div>
          </div>

          {/* Cryptographic SHA-256 Ledger Hash */}
          <div className="p-3 bg-[var(--bg-subtle)] rounded-lg border border-[var(--border-card)] space-y-1">
            <div className="flex items-center gap-1.5 text-[10px] text-[var(--text-muted)] font-mono-data font-semibold">
              <Hash className="w-3.5 h-3.5 text-amber-500" />
              <span>IMMUTABLE LEDGER HASH (SHA-256):</span>
            </div>
            <div className="font-mono-data text-[10px] text-amber-600 dark:text-amber-400 break-all">
              {docs.ledgerHash}
            </div>
          </div>
        </div>

        {/* Digital Signature & FastPass Biometric */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
                Driver Custody Signoff
              </div>
              <h2 className="text-base font-bold text-[var(--text-main)]">
                Marcus Vance ({ingress.carrierName})
              </h2>
            </div>
            {!docs.isBiometricFastPass && (
              <button
                onClick={clearCanvas}
                className="text-xs text-[var(--text-muted)] hover:text-[var(--text-main)] underline cursor-pointer"
              >
                Clear Pad
              </button>
            )}
          </div>

          {docs.isBiometricFastPass ? (
            <div className="h-36 rounded-xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-300 dark:border-emerald-800 flex flex-col items-center justify-center space-y-2">
              <Fingerprint className="w-10 h-10 text-emerald-600 dark:text-emerald-400 animate-pulse" />
              <div className="font-bold text-sm text-emerald-800 dark:text-emerald-300">
                BIOMETRIC FASTPASS AUTHENTICATED
              </div>
              <div className="text-xs text-[var(--text-muted)] font-mono-data">
                Driver Public Key #NX-BIO-99201 Verified
              </div>
            </div>
          ) : (
            <div className="border border-[var(--border-card)] rounded-xl bg-[var(--bg-subtle)] overflow-hidden">
              <canvas
                ref={canvasRef}
                width={500}
                height={144}
                className="w-full h-36 cursor-crosshair touch-none"
                onMouseDown={startDraw}
                onMouseMove={draw}
                onMouseUp={stopDraw}
                onMouseLeave={stopDraw}
                onTouchStart={startDraw}
                onTouchMove={draw}
                onTouchEnd={stopDraw}
              />
            </div>
          )}

          <div className="flex flex-wrap items-center justify-between gap-3 pt-1">
            <button
              onClick={toggleBiometric}
              className="flex items-center gap-2 text-xs text-sky-600 dark:text-sky-400 hover:underline font-semibold cursor-pointer"
            >
              <Fingerprint className="w-4 h-4" />
              <span>{docs.isBiometricFastPass ? 'SWITCH TO STYLUS SIGNATURE' : 'USE BIOMETRIC FASTPASS'}</span>
            </button>

            <button
              onClick={handleCompletePickup}
              disabled={!hasSignature && !docs.isBiometricFastPass}
              className="bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-bold text-xs px-5 py-2.5 rounded-lg flex items-center gap-2 shadow-xs transition cursor-pointer"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>{docs.outboundDispatched ? 'OUTBOUND DISPATCHED' : 'COMPLETE PICKUP & DISPATCH'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Full e-BOL View Modal */}
      {showBolModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-[var(--bg-card)] border border-[var(--border-card)] rounded-xl max-w-2xl w-full p-6 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
              <div>
                <div className="text-xs text-amber-600 dark:text-amber-400 font-mono-data font-bold">
                  OFFICIAL BILL OF LADING
                </div>
                <div className="text-lg font-bold text-[var(--text-main)]">
                  Electronic BOL #{ingress.orderNumber}
                </div>
              </div>
              <button
                onClick={() => setShowBolModal(false)}
                className="p-1 rounded-lg text-[var(--text-muted)] hover:text-[var(--text-main)] cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3 text-xs text-[var(--text-main)]">
              <div className="grid grid-cols-2 gap-4 bg-[var(--bg-subtle)] p-3 rounded-lg">
                <div>
                  <span className="text-[var(--text-muted)]">Origin Facility:</span>
                  <div className="font-bold">Dallas North Logistics Center (Bay 14)</div>
                </div>
                <div>
                  <span className="text-[var(--text-muted)]">Consignee Destination:</span>
                  <div className="font-bold">Target Regional DC Dallas #8492 (Bay 08)</div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 bg-[var(--bg-subtle)] p-3 rounded-lg">
                <div>
                  <span className="text-[var(--text-muted)]">Assigned Carrier & Tractor:</span>
                  <div className="font-bold">Marcus Vance (Cab #{ingress.tractorPlate})</div>
                </div>
                <div>
                  <span className="text-[var(--text-muted)]">Bolt Seal Authenticated:</span>
                  <div className="font-mono-data font-bold text-emerald-600 dark:text-emerald-400">
                    {ingress.boltSealExpected}
                  </div>
                </div>
              </div>

              <div className="bg-[var(--bg-subtle)] p-3 rounded-lg">
                <span className="text-[var(--text-muted)]">Cargo Description:</span>
                <div className="font-bold mt-1">24 Standard 48x40 Cold-Chain Skids (42,300 lbs gross)</div>
                <div className="text-sky-600 dark:text-sky-400 text-[11px] mt-0.5">
                  Continuous -20.2°C Cold Chain Protocol Maintained
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-3 border-t border-[var(--border-card)]">
              <button
                onClick={() => setShowBolModal(false)}
                className="px-4 py-2 rounded-lg bg-[var(--bg-subtle)] text-[var(--text-muted)] text-xs hover:text-[var(--text-main)] font-semibold cursor-pointer"
              >
                CLOSE
              </button>
              <button
                onClick={() => setShowBolModal(false)}
                className="flex items-center gap-2 px-4 py-2 rounded-lg bg-amber-600 hover:bg-amber-700 text-white font-bold text-xs shadow-xs cursor-pointer"
              >
                <Download className="w-3.5 h-3.5" />
                <span>EXPORT VERIFIED e-BOL</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
