import React from 'react';
import { ShieldCheck, Activity, Terminal } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';

export const Footer: React.FC = () => {
  const { font } = useLogistics();
  const isTimes = font === 'times';

  return (
    <footer className={`mt-12 border-t border-[var(--border-card)] bg-[var(--bg-card)] py-6 text-xs text-[var(--text-muted)] ${isTimes ? 'font-times' : 'font-calibri'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-emerald-500" />
          <span className="font-semibold text-[var(--text-main)]">NEXUS SYSTEM v2.4</span>
          <span>• CAN-Bus Telematics & Cryptographic Proof of Custody</span>
        </div>

        <div className="flex items-center gap-4 font-mono-data text-[11px]">
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
            LATENCY: 14ms
          </span>
          <span>SEC: DFW-GATE-04</span>
          <span>SLA: 99.98%</span>
        </div>
      </div>
    </footer>
  );
};
