import React, { useState } from 'react';
import { Truck, Lock, Unlock, CheckCircle2, RefreshCw, Box, AlertCircle, Thermometer } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';

export const YardStowageView: React.FC = () => {
  const { yard, font, toggleDockLock, purgePneumatics, selectPallet } = useLogistics();
  const [isPurging, setIsPurging] = useState(false);

  const selectedPallet = yard.pallets.find((p) => p.id === yard.selectedPalletId) || yard.pallets[0];

  const handlePurge = async () => {
    setIsPurging(true);
    await purgePneumatics();
    setIsPurging(false);
  };

  const isTimes = font === 'times';

  return (
    <div className={`space-y-6 ${isTimes ? 'font-times' : 'font-calibri'}`}>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left 2 Cols: Tactical Yard Map & Safety Checklist */}
        <div className="lg:col-span-2 space-y-6">
          {/* Target Dock Bay Card */}
          <div className="clean-card p-5 space-y-4">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-[var(--border-card)] pb-3">
              <div>
                <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
                  Assigned Inbound Staging
                </div>
                <div className="text-2xl font-black text-[var(--text-main)] tracking-tight">
                  DOCK BAY 14 (NORTH)
                </div>
                <div className="text-xs text-[var(--text-muted)] mt-0.5">
                  Sector B • Row C Staging Area • Target Temp: -20.0°C Reefer Pre-Cooled
                </div>
              </div>
              <span className="px-3 py-1 rounded-full bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300 text-xs font-mono-data font-bold">
                24 / 24 SKIDS STAGED (100%)
              </span>
            </div>

            {/* Clean SVG Yard Layout */}
            <div className="relative h-48 bg-slate-950 rounded-xl overflow-hidden p-3 border border-[var(--border-card)]">
              <svg className="w-full h-full" viewBox="0 0 600 200">
                <defs>
                  <pattern id="cleanYardGrid" width="30" height="30" patternUnits="userSpaceOnUse">
                    <path d="M 30 0 L 0 0 0 30" fill="none" stroke="#1E293B" strokeWidth="0.8" />
                  </pattern>
                </defs>
                <rect width="600" height="200" fill="url(#cleanYardGrid)" />

                {/* Road Ingress Track */}
                <path d="M 40 180 L 150 180 L 150 70 L 450 70" fill="none" stroke="#334155" strokeWidth="20" strokeLinecap="round" />
                <path d="M 40 180 L 150 180 L 150 70 L 450 70" fill="none" stroke="#F59E0B" strokeWidth="3" strokeDasharray="6 6" />

                {/* Dock Bays 10 - 15 */}
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
                        fill={isAssigned ? '#1E293B' : '#0F172A'}
                        stroke={isAssigned ? '#F59E0B' : '#334155'}
                        strokeWidth={isAssigned ? '2.5' : '1'}
                      />
                      <text
                        x={x + 23}
                        y={45}
                        fill={isAssigned ? '#F59E0B' : '#94A3B8'}
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

                {/* Truck marker */}
                <g transform="translate(150, 70)">
                  <circle cx="0" cy="0" r="11" fill="#38BDF8" />
                  <Truck className="w-4 h-4 text-slate-900 -translate-x-2 -translate-y-2" />
                </g>
              </svg>
            </div>
          </div>

          {/* Safety Protocols Checklist Card */}
          <div className="clean-card p-5 space-y-4">
            <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
              Dock Safety Interlock Verification
            </div>

            <div className="space-y-3 text-xs">
              {/* Check 1: Ultrasonic Wheel Chocks */}
              <div className="flex items-center justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-emerald-600 dark:text-emerald-400 shrink-0" />
                  <div>
                    <div className="font-bold text-[var(--text-main)]">Ultrasonic Wheel Chocks & Glad Hand Lock</div>
                    <div className="text-[var(--text-muted)]">Dual wireless ground sensors seated firmly under trailer wheels</div>
                  </div>
                </div>
                <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300 font-mono-data font-bold text-[11px]">
                  VERIFIED
                </span>
              </div>

              {/* Check 2: 270° Door Latches */}
              <div className="flex items-center justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-emerald-600 dark:text-emerald-400 shrink-0" />
                  <div>
                    <div className="font-bold text-[var(--text-main)]">Trailer Swing Doors Latched to Sides</div>
                    <div className="text-[var(--text-muted)]">Full 270° magnetic safety hold confirmed before bay entry</div>
                  </div>
                </div>
                <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300 font-mono-data font-bold text-[11px]">
                  VERIFIED
                </span>
              </div>

              {/* Check 3: Hydraulic RIG Lock Hook */}
              <div className="flex items-center justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)]">
                <div className="flex items-center gap-3">
                  {yard.dockLockEngaged ? (
                    <Lock className="w-5 h-5 text-emerald-600 dark:text-emerald-400 shrink-0" />
                  ) : (
                    <Unlock className="w-5 h-5 text-amber-500 shrink-0" />
                  )}
                  <div>
                    <div className="font-bold text-[var(--text-main)]">Hydraulic RIG Dock Lock Hook</div>
                    <div className="text-[var(--text-muted)]">
                      {yard.dockLockEngaged
                        ? 'Rear impact guard captured • Green interior dock light activated'
                        : 'Hook open • Ready for engagement'}
                    </div>
                  </div>
                </div>
                <button
                  onClick={toggleDockLock}
                  className={`px-3.5 py-1.5 rounded-lg font-bold text-xs shadow-xs transition cursor-pointer ${
                    yard.dockLockEngaged
                      ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                      : 'bg-amber-600 hover:bg-amber-700 text-white'
                  }`}
                >
                  {yard.dockLockEngaged ? 'DISENGAGE HOOK' : 'ENGAGE DOCK LOCK'}
                </button>
              </div>
            </div>

            {/* Pneumatics Purge Action */}
            <div className="pt-2 flex items-center justify-between text-xs text-[var(--text-muted)] border-t border-[var(--border-card)]">
              <span>Chock Air Manifold Status:</span>
              <button
                onClick={handlePurge}
                disabled={isPurging}
                className="flex items-center gap-1.5 text-sky-600 dark:text-sky-400 font-semibold hover:underline cursor-pointer"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${isPurging ? 'animate-spin' : ''}`} />
                <span>{isPurging ? 'PURGING AIR PRESSURE...' : 'RE-CYCLE PNEUMATICS'}</span>
              </button>
            </div>
          </div>
        </div>

        {/* Right Col: 53ft Reefer Blueprint & Axle Weight Balancer */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-sky-600 dark:text-sky-400 font-mono-data font-bold uppercase tracking-wider">
                53ft Reefer Blueprint
              </div>
              <h2 className="text-base font-bold text-[var(--text-main)]">
                18 / 24 Skids Loaded (75%)
              </h2>
            </div>
            <span className="px-2.5 py-0.5 rounded-full bg-sky-100 dark:bg-sky-900/40 text-sky-800 dark:text-sky-300 text-xs font-mono-data font-bold">
              -20.2°C COLD CHAIN
            </span>
          </div>

          {/* Axle Weight Distribution Meters */}
          <div className="space-y-2 text-xs">
            <div className="text-[var(--text-muted)] font-mono-data font-semibold">
              AXLE WEIGHT DISTRIBUTION:
            </div>
            <div className="grid grid-cols-3 gap-2 font-mono-data text-[11px]">
              <div className="bg-[var(--bg-subtle)] p-2 rounded-lg border border-[var(--border-card)] text-center">
                <div className="text-[var(--text-muted)]">Steer</div>
                <div className="font-bold text-[var(--text-main)]">11,800 lb</div>
              </div>
              <div className="bg-[var(--bg-subtle)] p-2 rounded-lg border border-[var(--border-card)] text-center">
                <div className="text-[var(--text-muted)]">Drive</div>
                <div className="font-bold text-[var(--text-main)]">33,400 lb</div>
              </div>
              <div className="bg-[var(--bg-subtle)] p-2 rounded-lg border border-[var(--border-card)] text-center">
                <div className="text-[var(--text-muted)]">Tandem</div>
                <div className="font-bold text-[var(--text-main)]">32,900 lb</div>
              </div>
            </div>
          </div>

          {/* 12 x 2 Trailer Pallet Matrix */}
          <div className="space-y-1.5">
            <div className="flex justify-between text-[10px] text-[var(--text-muted)] font-mono-data px-1">
              <span>NOSE (PORT)</span>
              <span>REAR (STARBOARD)</span>
            </div>

            <div className="max-h-56 overflow-y-auto space-y-1 pr-1">
              {Array.from({ length: 12 }).map((_, row) => {
                const port = yard.pallets[row * 2];
                const star = yard.pallets[row * 2 + 1];
                return (
                  <div key={row} className="grid grid-cols-2 gap-2">
                    {[port, star].map((p) => {
                      if (!p) return null;
                      const isSelected = selectedPallet?.id === p.id;
                      return (
                        <button
                          key={p.id}
                          onClick={() => selectPallet(p.id)}
                          className={`p-2 rounded-lg text-left border transition text-[11px] flex justify-between items-center cursor-pointer ${
                            isSelected
                              ? 'border-amber-500 bg-amber-50 dark:bg-amber-950/30 text-amber-900 dark:text-amber-200'
                              : 'border-[var(--border-card)] bg-[var(--bg-subtle)] text-[var(--text-muted)] hover:border-[var(--border-subtle)]'
                          }`}
                        >
                          <span className="font-mono-data font-bold">BAY #{p.id}</span>
                          <span className="text-[var(--text-main)] font-semibold">{p.weightLbs} lb</span>
                        </button>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Selected Pallet Lot Inspector */}
          {selectedPallet && (
            <div className="bg-[var(--bg-subtle)] border border-amber-500/40 rounded-xl p-3.5 space-y-2 text-xs">
              <div className="flex justify-between items-center font-bold text-[var(--text-main)]">
                <span>INSPECTION: PALLET #{selectedPallet.id}</span>
                <span className="text-emerald-600 dark:text-emerald-400 font-mono-data">{selectedPallet.status}</span>
              </div>
              <div className="grid grid-cols-2 gap-2 text-[11px]">
                <div>
                  <span className="text-[var(--text-muted)]">Lot:</span>{' '}
                  <strong className="text-[var(--text-main)] font-mono-data">{selectedPallet.lotNumber}</strong>
                </div>
                <div>
                  <span className="text-[var(--text-muted)]">RFID Tag:</span>{' '}
                  <strong className="text-[var(--text-main)] font-mono-data">{selectedPallet.rfidTag}</strong>
                </div>
                <div>
                  <span className="text-[var(--text-muted)]">Tare:</span>{' '}
                  <strong className="text-[var(--text-main)]">{selectedPallet.weightLbs} lbs</strong>
                </div>
                <div>
                  <span className="text-[var(--text-muted)]">Temp:</span>{' '}
                  <strong className="text-sky-600 dark:text-sky-400 font-mono-data">{selectedPallet.temperatureC.toFixed(1)}°C</strong>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
