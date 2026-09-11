import React, { useState } from 'react';
import { Truck, Lock, Unlock, AlertCircle, RefreshCw, CheckCircle2, ShieldCheck, Thermometer, Box } from 'lucide-react';
import { LogisticsState, PalletSkid } from '../types';

interface Props {
  state: LogisticsState;
  onUpdateState: (updates: Partial<LogisticsState>) => void;
  onNotify: (msg: string) => void;
}

export const YardDockModule: React.FC<Props> = ({ state, onUpdateState, onNotify }) => {
  const [purging, setPurging] = useState(false);
  const [selectedPallet, setSelectedPallet] = useState<PalletSkid | null>(state.pallets[0] || null);

  const handleToggleDockLock = () => {
    const next = !state.dockLockEngaged;
    onUpdateState({ dockLockEngaged: next });
    onNotify(next ? 'Hydraulic Dock Lock Hook Engaged. Green Light On.' : 'Dock Lock Hook Disengaged. Red Light On.');
  };

  const handlePneumaticPurge = () => {
    setPurging(true);
    setTimeout(() => {
      setPurging(false);
      onNotify('Pneumatic chock manifold purged and re-seated. 6-inch gap fault cleared.');
    }, 2000);
  };

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left 2 Cols: Yard Map & Safety Checklist */}
        <div className="lg:col-span-2 space-y-6">
          {/* Target Bay & Tactical Map Card */}
          <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-[#1C2B4E] pb-3">
              <div>
                <div className="text-xs text-[#F59E0B] font-mono font-bold">ASSIGNED TARGET BAY</div>
                <div className="text-2xl font-black text-white tracking-wide">{state.assignedBay}</div>
                <div className="text-xs text-[#8E9BB5]">Sector B-North • Staging Row C • Pre-Cool: -20.4°C Verified</div>
              </div>
              <span className="px-3 py-1 rounded bg-[#51E77B]/20 text-[#51E77B] text-xs font-mono font-bold border border-[#51E77B]/40">
                100% STAGED (24/24)
              </span>
            </div>

            {/* Tactical Yard Vector Canvas */}
            <div className="relative h-48 bg-[#090F1E] border border-[#2A3B66] rounded-xl overflow-hidden p-3">
              <svg className="w-full h-full" viewBox="0 0 600 200">
                {/* Grid Lines */}
                <defs>
                  <pattern id="yardGrid" width="30" height="30" patternUnits="userSpaceOnUse">
                    <path d="M 30 0 L 0 0 0 30" fill="none" stroke="#162444" strokeWidth="0.8" />
                  </pattern>
                </defs>
                <rect width="600" height="200" fill="url(#yardGrid)" />

                {/* Road Ingress Track */}
                <path d="M 40 180 L 150 180 L 150 70 L 450 70" fill="none" stroke="#2A3B66" strokeWidth="24" strokeLinecap="round" />
                <path d="M 40 180 L 150 180 L 150 70 L 450 70" fill="none" stroke="#F59E0B" strokeWidth="3" strokeDasharray="6 6" />

                {/* Dock Bays 10, 11, 12, 13, 14, 15 */}
                {[10, 11, 12, 13, 14, 15].map((bay, idx) => {
                  const x = 200 + idx * 60;
                  const isAssigned = bay === 14;
                  return (
                    <g key={bay}>
                      <rect
                        x={x}
                        y={20}
                        width="46"
                        height="40"
                        rx="4"
                        fill={isAssigned ? '#1E3059' : '#101B35'}
                        stroke={isAssigned ? '#F59E0B' : '#2A3B66'}
                        strokeWidth={isAssigned ? '2' : '1'}
                      />
                      <text
                        x={x + 23}
                        y={45}
                        fill={isAssigned ? '#F59E0B' : '#8E9BB5'}
                        fontSize="12"
                        fontFamily="monospace"
                        fontWeight="bold"
                        textAnchor="middle"
                      >
                        B{bay}
                      </text>
                      {isAssigned && (
                        <circle cx={x + 23} cy={70} r="6" fill="#F59E0B" className="animate-ping" />
                      )}
                    </g>
                  );
                })}

                {/* Inbound Truck Marker */}
                <g transform="translate(150, 70)">
                  <circle cx="0" cy="0" r="10" fill="#4CD7F6" />
                  <Truck className="w-4 h-4 text-black -translate-x-2 -translate-y-2" />
                </g>
              </svg>
            </div>
          </div>

          {/* Safety Protocols Checklist Card */}
          <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
            <div className="text-xs text-[#F59E0B] font-mono font-bold uppercase tracking-wider">
              Dock Safety Protocol Verification
            </div>

            <div className="space-y-3 text-xs">
              {/* Check 1 */}
              <div className="flex items-center justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-[#51E77B]" />
                  <div>
                    <div className="font-bold text-white">Wheel Chocks & Glad Hand Lock</div>
                    <div className="text-[#8E9BB5]">Dual ultrasonic sensors seated firmly</div>
                  </div>
                </div>
                <span className="px-2 py-0.5 rounded bg-[#51E77B]/20 text-[#51E77B] font-mono font-bold">VERIFIED</span>
              </div>

              {/* Check 2 */}
              <div className="flex items-center justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-[#51E77B]" />
                  <div>
                    <div className="font-bold text-white">Trailer Doors Swung & Latched</div>
                    <div className="text-[#8E9BB5]">Full 270° latching confirmed</div>
                  </div>
                </div>
                <span className="px-2 py-0.5 rounded bg-[#51E77B]/20 text-[#51E77B] font-mono font-bold">VERIFIED</span>
              </div>

              {/* Check 3: Interactive Dock Lock Hook */}
              <div className="flex items-center justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66]">
                <div className="flex items-center gap-3">
                  {state.dockLockEngaged ? (
                    <Lock className="w-5 h-5 text-[#51E77B]" />
                  ) : (
                    <Unlock className="w-5 h-5 text-[#F59E0B]" />
                  )}
                  <div>
                    <div className="font-bold text-white">Hydraulic Dock Lock Hook</div>
                    <div className="text-[#8E9BB5]">
                      {state.dockLockEngaged ? 'RIG bar captured • Green light on' : 'Ready for engagement'}
                    </div>
                  </div>
                </div>
                <button
                  onClick={handleToggleDockLock}
                  className={`px-3 py-1.5 rounded-lg font-bold transition cursor-pointer ${
                    state.dockLockEngaged
                      ? 'bg-[#51E77B] text-black hover:bg-[#43C769]'
                      : 'bg-[#F59E0B] text-black hover:bg-[#D97706]'
                  }`}
                >
                  {state.dockLockEngaged ? 'DISENGAGE HOOK' : 'ENGAGE HOOK'}
                </button>
              </div>
            </div>

            {/* Pneumatic Purge Workaround Option */}
            <div className="pt-2 flex items-center justify-between text-xs text-[#8E9BB5] border-t border-[#1C2B4E]">
              <span>Wheel Chock Air Manifold:</span>
              <button
                onClick={handlePneumaticPurge}
                disabled={purging}
                className="flex items-center gap-1.5 text-[#4CD7F6] hover:underline cursor-pointer"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${purging ? 'animate-spin' : ''}`} />
                <span>{purging ? 'PURGING AIR PRESSURE...' : 'RE-CYCLE PNEUMATICS'}</span>
              </button>
            </div>
          </div>
        </div>

        {/* Right Col: 53ft Reefer Blueprint & Axle Weight Balancer */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#4CD7F6] font-mono font-bold">53FT REEFER BLUEPRINT</div>
              <div className="text-base font-bold text-white">18 / 24 Skids Loaded (75%)</div>
            </div>
            <span className="px-2.5 py-1 rounded bg-[#4CD7F6]/20 text-[#4CD7F6] text-xs font-mono font-bold border border-[#4CD7F6]/40">
              -20.1°C REEFER
            </span>
          </div>

          {/* Axle Weight Meters */}
          <div className="space-y-2 text-xs">
            <div className="text-[#8E9BB5] font-mono">AXLE WEIGHT DISTRIBUTION:</div>
            <div className="grid grid-cols-3 gap-2 text-[11px] font-mono">
              <div className="bg-[#162444] p-2 rounded border border-[#2A3B66]">
                <div className="text-[#8E9BB5]">Steer</div>
                <div className="font-bold text-white">11,800 lb</div>
              </div>
              <div className="bg-[#162444] p-2 rounded border border-[#2A3B66]">
                <div className="text-[#8E9BB5]">Drive</div>
                <div className="font-bold text-white">33,400 lb</div>
              </div>
              <div className="bg-[#162444] p-2 rounded border border-[#2A3B66]">
                <div className="text-[#8E9BB5]">Tandem</div>
                <div className="font-bold text-white">32,900 lb</div>
              </div>
            </div>
          </div>

          {/* 12 x 2 Trailer Grid */}
          <div className="space-y-1.5">
            <div className="flex justify-between text-[10px] text-[#8E9BB5] font-mono px-1">
              <span>NOSE (PORT)</span>
              <span>REAR (STARBOARD)</span>
            </div>
            <div className="max-h-56 overflow-y-auto space-y-1 pr-1">
              {Array.from({ length: 12 }).map((_, row) => {
                const port = state.pallets[row * 2];
                const star = state.pallets[row * 2 + 1];
                return (
                  <div key={row} className="grid grid-cols-2 gap-2">
                    {[port, star].map((p) => {
                      if (!p) return null;
                      const isSelected = selectedPallet?.id === p.id;
                      return (
                        <button
                          key={p.id}
                          onClick={() => setSelectedPallet(p)}
                          className={`p-1.5 rounded text-left border transition text-[11px] flex justify-between items-center cursor-pointer ${
                            isSelected
                              ? 'border-[#F59E0B] bg-[#F59E0B]/20 text-[#F59E0B]'
                              : 'border-[#2A3B66] bg-[#162444] text-[#8E9BB5] hover:border-[#8E9BB5]'
                          }`}
                        >
                          <span className="font-mono font-bold">BAY #{p.id}</span>
                          <span>{p.weightLbs} lb</span>
                        </button>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Pallet Inspector Card */}
          {selectedPallet && (
            <div className="bg-[#162444] border border-[#F59E0B]/40 rounded-lg p-3 space-y-2 text-xs">
              <div className="flex justify-between items-center font-bold text-white">
                <span>INSPECTOR: PALLET #{selectedPallet.id}</span>
                <span className="text-[#51E77B] font-mono">{selectedPallet.status}</span>
              </div>
              <div className="grid grid-cols-2 gap-2 text-[11px]">
                <div>
                  <span className="text-[#8E9BB5]">Lot:</span> <strong className="text-white font-mono">{selectedPallet.lotNumber}</strong>
                </div>
                <div>
                  <span className="text-[#8E9BB5]">RFID:</span> <strong className="text-white font-mono">{selectedPallet.rfidTag}</strong>
                </div>
                <div>
                  <span className="text-[#8E9BB5]">Tare:</span> <strong className="text-white">{selectedPallet.weightLbs} lbs</strong>
                </div>
                <div>
                  <span className="text-[#8E9BB5]">Temp:</span> <strong className="text-[#4CD7F6]">{selectedPallet.temperatureC}°C</strong>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
