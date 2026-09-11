import React from 'react';
import { ShieldAlert, Radio, Clock, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { LogisticsState, OperationalTab } from '../types';

interface Props {
  state: LogisticsState;
  onTabChange: (tab: OperationalTab) => void;
  onSosClick: () => void;
}

export const HeaderHud: React.FC<Props> = ({ state, onTabChange, onSosClick }) => {
  const mins = Math.floor(state.tatSeconds / 60);
  const secs = state.tatSeconds % 60;
  const tatFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const tabs: { id: OperationalTab; label: string; icon: string }[] = [
    { id: 'ACTIVE_RUN', label: '1. Ingress & OCR Scan', icon: 'Gate' },
    { id: 'YARD_BAYS', label: '2. Yard & 53ft Stowage', icon: 'Dock' },
    { id: 'DOCS_EPOP', label: '3. Docs & e-POP Sign', icon: 'File' },
    { id: 'CONTROL_TOWER', label: '4. Control Tower & Cascade', icon: 'Radar' },
  ];

  return (
    <header className="border-b border-[#2A3B66] bg-[#0E172F] sticky top-0 z-40">
      {/* Top telemetry bar */}
      <div className="max-w-7xl mx-auto px-4 py-2.5 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#F59E0B] to-[#D97706] flex items-center justify-center font-bold text-black text-sm shadow-[0_0_12px_rgba(245,158,11,0.4)]">
              NX
            </div>
            <div>
              <div className="font-extrabold tracking-wider text-white text-sm">NEXUS LOGISTICS</div>
              <div className="text-[10px] text-[#8E9BB5] font-mono">FIRST-MILE OUTBOUND ORCHESTRATION</div>
            </div>
          </div>
          <span className="hidden md:inline text-[#2A3B66]">|</span>
          <div className="hidden md:flex items-center gap-2 text-[#8E9BB5] font-mono">
            <Radio className="w-3.5 h-3.5 text-[#51E77B] animate-pulse" />
            <span>DAL-01 INGRESS (32.7767° N, 96.7970° W)</span>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2 bg-[#162444] px-3 py-1 rounded-md border border-[#2A3B66] font-mono">
            <Clock className="w-3.5 h-3.5 text-[#F59E0B]" />
            <span className="text-[#8E9BB5]">TAT CLOCK:</span>
            <span className="font-bold text-[#F59E0B] text-sm">{tatFormatted}</span>
            <span className="text-[10px] text-[#51E77B]">(TARGET ≤ 25m)</span>
          </div>

          <button
            onClick={onSosClick}
            className="flex items-center gap-1.5 bg-[#FF5449]/20 hover:bg-[#FF5449]/30 text-[#FF5449] border border-[#FF5449]/50 px-3 py-1 rounded-md font-semibold transition cursor-pointer"
          >
            <ShieldAlert className="w-3.5 h-3.5" />
            <span>EMERGENCY SOS</span>
          </button>
        </div>
      </div>

      {/* Primary Navigation Tabs */}
      <div className="max-w-7xl mx-auto px-4 flex overflow-x-auto gap-2 border-t border-[#1C2B4E]">
        {tabs.map((tab) => {
          const isActive = state.activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className={`py-2.5 px-4 text-xs font-semibold tracking-wide uppercase transition-all whitespace-nowrap border-b-2 cursor-pointer flex items-center gap-2 ${
                isActive
                  ? 'border-[#F59E0B] text-[#F59E0B] bg-[#F59E0B]/10'
                  : 'border-transparent text-[#8E9BB5] hover:text-white hover:bg-[#162444]'
              }`}
            >
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>
    </header>
  );
};
