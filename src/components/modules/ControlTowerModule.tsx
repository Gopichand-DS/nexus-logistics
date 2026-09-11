import React from 'react';
import { AlertTriangle, Navigation, Clock, DollarSign, Send, CheckCircle2, Radio, ArrowRight } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';

export const ControlTowerModule: React.FC = () => {
  const {
    controlTower,
    font,
    approveProtocolAlpha,
    requestDockHold,
    selectBid,
    acceptBid,
  } = useLogistics();

  const mins = Math.floor(controlTower.auctionSeconds / 60);
  const secs = controlTower.auctionSeconds % 60;
  const timerFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const isTimes = font === 'times';

  return (
    <div className={`space-y-6 ${isTimes ? 'font-times' : 'font-calibri'}`}>
      {/* Highway Incident Banner */}
      <div className={`p-5 rounded-xl border flex flex-wrap items-center justify-between gap-4 transition ${
        controlTower.rerouteApproved
          ? 'bg-emerald-50 dark:bg-emerald-950/30 border-emerald-300 dark:border-emerald-800 text-emerald-900 dark:text-emerald-200'
          : 'bg-red-50 dark:bg-red-950/30 border-red-300 dark:border-red-800 text-red-900 dark:text-red-200'
      }`}>
        <div className="flex items-center gap-4">
          <AlertTriangle className={`w-7 h-7 shrink-0 ${controlTower.rerouteApproved ? 'text-emerald-600 dark:text-emerald-400' : 'text-red-600 dark:text-red-400 animate-pulse'}`} />
          <div>
            <div className="font-extrabold tracking-wide text-sm">
              {controlTower.rerouteApproved
                ? 'PROTOCOL ALPHA ACTIVE: LOOP 12 S BYPASS DISPATCHED'
                : 'HIGHWAY BOTTLENECK: I-35E SOUTHBOUND MM 142.4'}
            </div>
            <div className="text-xs text-[var(--text-muted)] mt-0.5">
              {controlTower.rerouteApproved
                ? 'Cab navigation confirmed bypass. Destination on-time arrival intact at 15:38 UTC.'
                : 'Multi-vehicle collision blocking 2 travel lanes. Unmitigated delay (+34m) risks $180 demurrage penalty.'}
            </div>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="px-3 py-1.5 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] text-xs font-mono-data">
            <span className="text-[var(--text-muted)]">CASCADE COUNTDOWN: </span>
            <span className="text-amber-600 dark:text-amber-400 font-bold">{timerFormatted}</span>
          </div>

          {!controlTower.rerouteApproved && (
            <button
              onClick={approveProtocolAlpha}
              className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold px-4 py-2 rounded-lg shadow-xs transition cursor-pointer"
            >
              EXECUTE PROTOCOL ALPHA
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Col: Highway Telematics Corridor Radar */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-sky-600 dark:text-sky-400 font-mono-data font-bold uppercase tracking-wider">
                Corridor Telematics
              </div>
              <h2 className="text-base font-bold text-[var(--text-main)]">
                Live Highway Radar (I-35E vs Loop 12)
              </h2>
            </div>
            <span className="px-2.5 py-0.5 rounded-full bg-amber-100 dark:bg-amber-900/40 text-amber-800 dark:text-amber-300 text-[10px] font-mono-data font-bold">
              TRACTOR #4412
            </span>
          </div>

          {/* SVG Vector Highway Corridor */}
          <div className="relative h-60 bg-slate-950 rounded-xl overflow-hidden p-2 border border-[var(--border-card)]">
            <svg className="w-full h-full" viewBox="0 0 500 240">
              {/* Origin Marker */}
              <circle cx="50" cy="120" r="8" fill="#F59E0B" />
              <text x="50" y="145" fill="#F59E0B" fontSize="10" fontFamily="monospace" textAnchor="middle">
                Dallas FC
              </text>

              {/* Destination Marker */}
              <circle cx="450" cy="120" r="8" fill="#10B981" />
              <text x="450" y="145" fill="#10B981" fontSize="10" fontFamily="monospace" textAnchor="middle">
                DC #8492
              </text>

              {/* Blocked I-35 Direct Route */}
              <path d="M 50 120 L 450 120" fill="none" stroke="#334155" strokeWidth="8" strokeLinecap="round" />
              <line x1="250" y1="100" x2="250" y2="140" stroke="#EF4444" strokeWidth="4" />
              <text x="250" y="90" fill="#EF4444" fontSize="10" fontFamily="monospace" textAnchor="middle" fontWeight="bold">
                INCIDENT MM 142 (+34m)
              </text>

              {/* Loop 12 Bypass Route */}
              <path
                d="M 50 120 Q 250 20 450 120"
                fill="none"
                stroke={controlTower.rerouteApproved ? '#10B981' : '#38BDF8'}
                strokeWidth={controlTower.rerouteApproved ? '6' : '3'}
                strokeDasharray={controlTower.rerouteApproved ? 'none' : '6 6'}
              />
              <text x="250" y="45" fill="#38BDF8" fontSize="10" fontFamily="monospace" textAnchor="middle">
                Loop 12 Bypass (+14m Recovered)
              </text>

              {/* Animated Truck Position Marker */}
              <circle
                cx={controlTower.rerouteApproved ? '180' : '120'}
                cy={controlTower.rerouteApproved ? '65' : '120'}
                r="7"
                fill="#F59E0B"
                className="animate-pulse"
              />
            </svg>
          </div>

          {/* Incident Mitigation Action Rows */}
          <div className="space-y-2 text-xs">
            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] items-center">
              <div>
                <div className="font-bold text-[var(--text-main)]">Protocol Alpha: Loop 12 S Bypass</div>
                <div className="text-[var(--text-muted)]">+$9.40 toll/fuel variance pre-approved • ETA: 15:38 UTC</div>
              </div>
              <button
                onClick={approveProtocolAlpha}
                disabled={controlTower.rerouteApproved}
                className="px-3 py-1.5 rounded-lg bg-amber-600 hover:bg-amber-700 disabled:opacity-40 text-white font-bold text-xs shadow-xs cursor-pointer"
              >
                {controlTower.rerouteApproved ? 'ACTIVE' : 'APPROVE'}
              </button>
            </div>

            <div className="flex justify-between p-3 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] items-center">
              <div>
                <div className="font-bold text-[var(--text-main)]">Protocol Bravo: Bay Window Hold</div>
                <div className="text-[var(--text-muted)]">Dallas DC Bay 08 dock slot held until 16:00 UTC (No fees)</div>
              </div>
              <button
                onClick={requestDockHold}
                disabled={controlTower.dockHoldApproved}
                className="px-3 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-700 disabled:opacity-40 text-white font-bold text-xs shadow-xs cursor-pointer"
              >
                {controlTower.dockHoldApproved ? 'HELD' : 'REQUEST'}
              </button>
            </div>
          </div>
        </div>

        {/* Right Col: Cascading Freight Auction Engine */}
        <div className="clean-card p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[var(--border-card)] pb-3">
            <div>
              <div className="text-[11px] text-amber-600 dark:text-amber-400 font-mono-data font-bold uppercase tracking-wider">
                Phase 2: Bind SLA Engine
              </div>
              <h2 className="text-base font-bold text-[var(--text-main)]">
                Cascading Freight Auction
              </h2>
            </div>
            <span className="px-2.5 py-0.5 rounded-full bg-red-100 dark:bg-red-900/40 text-red-800 dark:text-red-300 text-[10px] font-mono-data font-bold">
              $180 DEMURRAGE RISK
            </span>
          </div>

          <div className="space-y-3">
            {controlTower.bids.map((bid) => {
              const isSelected = controlTower.selectedBidId === bid.id;
              return (
                <div
                  key={bid.id}
                  onClick={() => selectBid(bid.id)}
                  className={`p-3.5 rounded-xl border transition cursor-pointer ${
                    isSelected
                      ? 'bg-amber-50 dark:bg-amber-950/20 border-amber-500 shadow-xs'
                      : 'bg-[var(--bg-subtle)] border-[var(--border-card)] hover:border-[var(--border-subtle)]'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-bold text-[var(--text-main)] text-sm flex items-center gap-2">
                        <span>{bid.carrierName}</span>
                        {bid.isLowest && (
                          <span className="px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/40 text-emerald-800 dark:text-emerald-300 text-[9px] font-mono-data font-bold">
                            LOWEST
                          </span>
                        )}
                      </div>
                      <div className="text-xs text-[var(--text-muted)] mt-0.5">
                        {bid.unitNumber} • {bid.equipmentType}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-lg font-black text-amber-600 dark:text-amber-400 font-mono-data">
                        ${bid.bidAmountUsd}
                      </div>
                      <div className="text-[10px] text-sky-600 dark:text-sky-400 font-mono-data">
                        {bid.distanceInfo}
                      </div>
                    </div>
                  </div>
                  <div className="text-[11px] text-[var(--text-muted)] mt-2 pt-2 border-t border-[var(--border-card)]">
                    {bid.protocolNote}
                  </div>
                </div>
              );
            })}
          </div>

          <div className="pt-2">
            <button
              onClick={acceptBid}
              className="w-full py-2.5 rounded-lg bg-amber-600 hover:bg-amber-700 text-white font-extrabold text-xs flex items-center justify-center gap-2 shadow-xs transition cursor-pointer"
            >
              <Send className="w-4 h-4" />
              <span>ACCEPT SELECTED BID & DISPATCH RELAY</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
