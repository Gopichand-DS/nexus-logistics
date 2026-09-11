import React from 'react';
import { AlertTriangle, Navigation, Clock, DollarSign, Send, CheckCircle2, Shield, Radio, ArrowRight } from 'lucide-react';
import { LogisticsState } from '../types';

interface Props {
  state: LogisticsState;
  onUpdateState: (updates: Partial<LogisticsState>) => void;
  onNotify: (msg: string) => void;
}

export const ControlTowerModule: React.FC<Props> = ({ state, onUpdateState, onNotify }) => {
  const handleApproveReroute = () => {
    onUpdateState({ rerouteApproved: true });
    onNotify('Protocol Alpha approved: Loop 12 Bypass pushed to Cab Nav. ETA revised to 15:38 UTC (+14m recovered).');
  };

  const handleRequestDockHold = () => {
    onUpdateState({ dockHoldApproved: true });
    onNotify('Protocol Bravo: Dallas DC #8492 Bay 08 dock reservation extended to 16:00 UTC. Late fees waived.');
  };

  const handleSelectBid = (bidId: string) => {
    onUpdateState({ selectedBidId: bidId });
  };

  const handleAcceptBid = () => {
    const selected = state.bids.find((b) => b.id === state.selectedBidId);
    if (!selected) return;
    onNotify(`Cascading relay bound to ${selected.carrierName} for $${selected.bidAmountUsd}. Unit dispatched.`);
  };

  const mins = Math.floor(state.auctionSeconds / 60);
  const secs = state.auctionSeconds % 60;
  const timerFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  return (
    <div className="space-y-6">
      {/* Incident Warning Banner */}
      <div className={`p-5 rounded-xl border flex flex-wrap items-center justify-between gap-4 ${
        state.rerouteApproved
          ? 'bg-[#51E77B]/10 border-[#51E77B]/40 text-[#51E77B]'
          : 'bg-[#FF5449]/10 border-[#FF5449]/40 text-[#FF5449]'
      }`}>
        <div className="flex items-center gap-4">
          <AlertTriangle className={`w-7 h-7 ${state.rerouteApproved ? 'text-[#51E77B]' : 'text-[#FF5449] animate-pulse'}`} />
          <div>
            <div className="font-extrabold tracking-wide text-sm">
              {state.rerouteApproved
                ? 'PROTOCOL ALPHA ACTIVE: LOOP 12 S BYPASS DISPATCHED'
                : 'CORRIDOR BOTTLENECK: I-35E SOUTHBOUND MM 142.4'}
            </div>
            <div className="text-xs text-[#8E9BB5] mt-0.5">
              {state.rerouteApproved
                ? 'Nav telemetry confirmed by tractor cab. On-time delivery ETA intact at 15:38 UTC.'
                : 'Multi-vehicle collision blocking lanes. Unmitigated arrival breach (+34 min) risks $180 demurrage bond.'}
            </div>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="bg-[#090F1E] px-3 py-1.5 rounded-lg border border-[#2A3B66] text-xs font-mono">
            <span className="text-[#8E9BB5]">CASCADE COUNTDOWN: </span>
            <span className="text-[#F59E0B] font-bold">{timerFormatted}</span>
          </div>

          {!state.rerouteApproved && (
            <button
              onClick={handleApproveReroute}
              className="bg-[#51E77B] hover:bg-[#43C769] text-black text-xs font-extrabold px-4 py-2 rounded-lg transition cursor-pointer"
            >
              EXECUTE PROTOCOL ALPHA
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Col: Tactical Corridor Vector Radar */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#4CD7F6] font-mono font-bold">TELEMATICS RADAR</div>
              <div className="text-base font-bold text-white">Live Highway Corridor (I-35E vs Loop 12)</div>
            </div>
            <span className="px-2 py-0.5 rounded bg-[#F59E0B]/20 text-[#F59E0B] text-[10px] font-mono font-bold">
              TRACTOR #4412
            </span>
          </div>

          {/* SVG Vector Highway Corridor */}
          <div className="relative h-64 bg-[#090F1E] border border-[#2A3B66] rounded-xl overflow-hidden p-2">
            <svg className="w-full h-full" viewBox="0 0 500 240">
              {/* Origin Marker */}
              <circle cx="50" cy="120" r="8" fill="#F59E0B" />
              <text x="50" y="145" fill="#F59E0B" fontSize="10" fontFamily="monospace" textAnchor="middle">
                Dallas FC
              </text>

              {/* Destination Marker */}
              <circle cx="450" cy="120" r="8" fill="#51E77B" />
              <text x="450" y="145" fill="#51E77B" fontSize="10" fontFamily="monospace" textAnchor="middle">
                DC #8492
              </text>

              {/* Blocked I-35 Route */}
              <path d="M 50 120 L 450 120" fill="none" stroke="#2A3B66" strokeWidth="8" strokeLinecap="round" />
              <line x1="250" y1="100" x2="250" y2="140" stroke="#FF5449" strokeWidth="4" />
              <text x="250" y="90" fill="#FF5449" fontSize="10" fontFamily="monospace" textAnchor="middle" fontWeight="bold">
                INCIDENT MM 142.4 (+34m)
              </text>

              {/* Loop 12 Bypass Route */}
              <path
                d="M 50 120 Q 250 20 450 120"
                fill="none"
                stroke={state.rerouteApproved ? '#51E77B' : '#4CD7F6'}
                strokeWidth={state.rerouteApproved ? '6' : '3'}
                strokeDasharray={state.rerouteApproved ? 'none' : '6 6'}
              />
              <text x="250" y="45" fill="#4CD7F6" fontSize="10" fontFamily="monospace" textAnchor="middle">
                Loop 12 Bypass (+14m Recovered)
              </text>

              {/* Truck Position Marker */}
              <circle
                cx={state.rerouteApproved ? '180' : '120'}
                cy={state.rerouteApproved ? '65' : '120'}
                r="7"
                fill="#F59E0B"
                className="animate-pulse"
              />
            </svg>
          </div>

          {/* Protocols List */}
          <div className="space-y-2 text-xs">
            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66] items-center">
              <div>
                <div className="font-bold text-white">Protocol Alpha: Loop 12 Bypass</div>
                <div className="text-[#8E9BB5]">+$9.40 fuel variance pre-approved • ETA: 15:38 UTC</div>
              </div>
              <button
                onClick={handleApproveReroute}
                disabled={state.rerouteApproved}
                className="px-3 py-1 rounded bg-[#F59E0B] hover:bg-[#D97706] disabled:opacity-40 text-black font-bold text-[11px] cursor-pointer"
              >
                {state.rerouteApproved ? 'ACTIVE' : 'APPROVE'}
              </button>
            </div>

            <div className="flex justify-between p-3 bg-[#162444] rounded-lg border border-[#2A3B66] items-center">
              <div>
                <div className="font-bold text-white">Protocol Bravo: Bay Window Hold</div>
                <div className="text-[#8E9BB5]">Dallas DC Bay 08 reservation held until 16:00 UTC</div>
              </div>
              <button
                onClick={handleRequestDockHold}
                disabled={state.dockHoldApproved}
                className="px-3 py-1 rounded bg-[#4CD7F6] hover:bg-[#38BFDD] disabled:opacity-40 text-black font-bold text-[11px] cursor-pointer"
              >
                {state.dockHoldApproved ? 'HELD' : 'REQUEST'}
              </button>
            </div>
          </div>
        </div>

        {/* Right Col: Cascading Freight Auction Engine */}
        <div className="bg-[#101B35] border border-[#2A3B66] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#1C2B4E] pb-3">
            <div>
              <div className="text-xs text-[#F59E0B] font-mono font-bold">PHASE 2 BIND SLA ENGINE</div>
              <div className="text-base font-bold text-white">Cascading Freight Auction</div>
            </div>
            <span className="px-2 py-0.5 rounded bg-[#FF5449]/20 text-[#FF5449] text-[10px] font-mono font-bold">
              $180 DEMURRAGE RISK
            </span>
          </div>

          <div className="space-y-3">
            {state.bids.map((bid) => {
              const isSelected = state.selectedBidId === bid.id;
              return (
                <div
                  key={bid.id}
                  onClick={() => handleSelectBid(bid.id)}
                  className={`p-3.5 rounded-xl border transition cursor-pointer ${
                    isSelected
                      ? 'bg-[#1E3059] border-[#F59E0B]'
                      : 'bg-[#162444] border-[#2A3B66] hover:border-[#8E9BB5]'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-bold text-white text-sm flex items-center gap-2">
                        <span>{bid.carrierName}</span>
                        {bid.isLowest && (
                          <span className="px-2 py-0.5 rounded bg-[#51E77B]/20 text-[#51E77B] text-[9px] font-mono font-bold">
                            LOWEST
                          </span>
                        )}
                      </div>
                      <div className="text-xs text-[#8E9BB5] mt-0.5">
                        {bid.unitNumber} • {bid.equipmentType}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-lg font-black text-[#F59E0B] font-mono">${bid.bidAmountUsd}</div>
                      <div className="text-[10px] text-[#4CD7F6] font-mono">{bid.distanceInfo}</div>
                    </div>
                  </div>
                  <div className="text-[11px] text-[#8E9BB5] mt-2 pt-2 border-t border-[#1C2B4E]">
                    {bid.protocolNote}
                  </div>
                </div>
              );
            })}
          </div>

          <div className="pt-2">
            <button
              onClick={handleAcceptBid}
              className="w-full py-2.5 rounded-lg bg-[#F59E0B] hover:bg-[#D97706] text-black font-extrabold text-xs flex items-center justify-center gap-2 shadow-md transition cursor-pointer"
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
