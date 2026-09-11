import React, { useRef, useState, useEffect } from 'react';
import { FileText, Award, Fingerprint, Download, CheckCircle2, Shield, Hash } from 'lucide-react';
import { LogisticsState } from '../types';

interface Props {
  state: LogisticsState;
  onUpdateState: (updates: Partial<LogisticsState>) => void;
  onNotify: (msg: string) => void;
}

export const DocsEpopModule: React.FC<Props> = ({ state, onUpdateState, onNotify }) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [hasSignature, setHasSignature] = useState(false);
  const [showBolModal, setShowBolModal] = useState(false);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.strokeStyle = '#F59E0B';
    ctx.lineWidth = 2.5;
    ctx.lineCap = 'round';
  }, []);

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

  const clearSignature = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    setHasSignature(false);
  };

  const handleCompletePickup = () => {
    onUpdateState({ outboundDispatched: true });
    onNotify('Pickup completed! Outbound GPS dispatch & real-time telematics tracking engaged.');
  };

  return (
    <div className="space-y-6">
      {/* SLA Performance Bonus Card */}
      <div className="p-5 bg-gradient-to-r from-[#51E77B]/15 to-[#51E77B]/5 border border-[#51E77B]/40 rounded-xl flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-[#51E77B]/20 border border-[#51E77B] flex items-center justify-center text-[#51E77B]">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <span className="px-2.5 py-0.5 rounded bg-[#51E77B]/20 text-[#51E77B] text-[10px] font-mono font-bold">
              SLA BONUS QUALIFIED
            </span>
            <div className="text-xl font-extrabold text-white mt-1">+$45.00 PAYOUT CREDIT</div>
            <div className="text-xs text-[#8E9BB5]">
              Turnaround Time: 19m 40s (Exceeded 25:00 min target SLA threshold by 5m 20s)
            </div>
          </div>
        </div>

        <button
          onClick={() => setShowBolModal(true)}
          className="flex items-center gap-2 bg-[#162444] hover:bg-[#1E3059] text-[#F59E0B] border border-[#F59E0B]/50 px-4 py-2 rounded-lg text-xs font-bold transition cursor-pointer"
        >
          <FileText className="w-4 h-4" />
          <span>VIEW CRYPTOGRAPHIC e-BOL</span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chain of Custody Card */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="text-xs text-[#F59E0B] font-mono font-bold uppercase tracking-wider">
            Chain of Custody Transfer
          </div>

          <div className="space-y-3 text-xs">
            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
              <span className="text-[#8E9BB5]">Order Manifest:</span>
              <span className="font-mono text-white font-bold">{state.orderNumber}</span>
            </div>
            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
              <span className="text-[#8E9BB5]">Shipper Loader:</span>
              <span className="text-white font-bold">Marcus Vance (#MV-901) - Confirmed</span>
            </div>
            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
              <span className="text-[#8E9BB5]">Bolt Seal Stamp:</span>
              <span className="font-mono text-[#51E77B] font-bold">{state.boltSealExpected} (Verified)</span>
            </div>
            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
              <span className="text-[#8E9BB5]">Cold Chain Temp:</span>
              <span className="font-mono text-[#4CD7F6] font-bold">-20.1°C Continuously Locked</span>
            </div>
          </div>

          {/* Cryptographic Hash Badge */}
          <div className="p-3 bg-[#090F1E] rounded-lg border border-[#2A3B66] space-y-1">
            <div className="flex items-center gap-1.5 text-[10px] text-[#8E9BB5] font-mono">
              <Hash className="w-3.5 h-3.5 text-[#F59E0B]" />
              <span>SHA-256 IMMUTABLE LEDGER HASH:</span>
            </div>
            <div className="font-mono text-[10px] text-[#F59E0B] break-all">
              e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
            </div>
          </div>
        </div>

        {/* Digital Signature & FastPass Biometric */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#F59E0B] font-mono font-bold">DRIVER CUSTODY SIGNOFF</div>
              <div className="text-base font-bold text-white">Driver: Marcus Vance (Nexus Tier-1)</div>
            </div>
            {!state.isBiometricFastPass && (
              <button
                onClick={clearSignature}
                className="text-xs text-[#8E9BB5] hover:text-white underline cursor-pointer"
              >
                Clear
              </button>
            )}
          </div>

          {state.isBiometricFastPass ? (
            <div className="h-36 bg-[#51E77B]/10 border border-[#51E77B]/40 rounded-xl flex flex-col items-center justify-center space-y-2">
              <Fingerprint className="w-10 h-10 text-[#51E77B] animate-pulse" />
              <div className="font-bold text-sm text-[#51E77B]">BIOMETRIC FASTPASS AUTHENTICATED</div>
              <div className="text-xs text-[#8E9BB5] font-mono">Driver Key #NX-BIO-99201 Verified</div>
            </div>
          ) : (
            <div className="border border-[#2A3B66] rounded-xl bg-[#090F1E] overflow-hidden">
              <canvas
                ref={canvasRef}
                width={500}
                height={140}
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
              onClick={() => {
                const next = !state.isBiometricFastPass;
                onUpdateState({ isBiometricFastPass: next });
                onNotify(next ? 'Switched to Biometric FastPass Authentication' : 'Switched to manual stylus signature');
              }}
              className="flex items-center gap-2 text-xs text-[#4CD7F6] hover:underline cursor-pointer"
            >
              <Fingerprint className="w-4 h-4" />
              <span>{state.isBiometricFastPass ? 'USE MANUAL SIGNATURE' : 'USE BIOMETRIC FASTPASS'}</span>
            </button>

            <button
              onClick={handleCompletePickup}
              disabled={!hasSignature && !state.isBiometricFastPass}
              className="bg-[#51E77B] hover:bg-[#43C769] disabled:opacity-50 text-black font-bold text-xs px-5 py-2.5 rounded-lg flex items-center gap-2 shadow-md transition cursor-pointer"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>{state.outboundDispatched ? 'TRACKING ENGAGED' : 'COMPLETE PICKUP & DISPATCH'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Full e-BOL Modal */}
      {showBolModal && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50">
          <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl max-w-2xl w-full p-6 space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
              <div>
                <div className="text-xs text-[#F59E0B] font-mono">CRYPTOGRAPHIC PROOF OF PICKUP</div>
                <div className="text-lg font-bold text-white">Electronic Bill of Lading (e-BOL) #{state.orderNumber}</div>
              </div>
              <span className="px-2.5 py-1 rounded bg-[#51E77B]/20 text-[#51E77B] text-xs font-mono font-bold">
                LEDGER STAMPED
              </span>
            </div>

            <div className="space-y-3 text-xs text-[#E0E6F8]">
              <div className="grid grid-cols-2 gap-4 bg-[#162444] p-3 rounded-lg">
                <div>
                  <span className="text-[#8E9BB5]">Origin Facility:</span>
                  <div className="font-bold">Dallas North Logistics FC (Bay 14)</div>
                </div>
                <div>
                  <span className="text-[#8E9BB5]">Consignee Destination:</span>
                  <div className="font-bold">Target Regional DC Dallas #8492 (Bay 08)</div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 bg-[#162444] p-3 rounded-lg">
                <div>
                  <span className="text-[#8E9BB5]">Assigned Carrier:</span>
                  <div className="font-bold">Marcus Vance (Cab #9842, Trailer #TL-9023)</div>
                </div>
                <div>
                  <span className="text-[#8E9BB5]">Bolt Seal Authenticated:</span>
                  <div className="font-mono font-bold text-[#51E77B]">{state.boltSealExpected}</div>
                </div>
              </div>

              <div className="bg-[#162444] p-3 rounded-lg">
                <span className="text-[#8E9BB5]">Cargo Manifest:</span>
                <div className="font-bold mt-1">24 Standard 48x40 FMCG Skids (42,300 lbs gross)</div>
                <div className="text-[#4CD7F6] text-[11px] mt-0.5">Continuous -20.0°C Cold Chain Reefer Protocol Verified</div>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-3 border-t border-[#1C2B4E]">
              <button
                onClick={() => setShowBolModal(false)}
                className="px-4 py-2 rounded-lg bg-[#162444] text-[#8E9BB5] text-xs hover:text-white cursor-pointer"
              >
                CLOSE
              </button>
              <button
                onClick={() => {
                  onNotify('e-BOL PDF exported with SHA-256 validation stamp.');
                  setShowBolModal(false);
                }}
                className="flex items-center gap-2 px-4 py-2 rounded-lg bg-[#F59E0B] text-black font-bold text-xs hover:bg-[#D97706] cursor-pointer"
              >
                <Download className="w-3.5 h-3.5" />
                <span>EXPORT SIGNED e-BOL</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
