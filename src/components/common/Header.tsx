import React from 'react';
import { Radio, Clock, AlertTriangle, ShieldAlert, Sun, Moon, Type } from 'lucide-react';
import { useLogistics } from '../../context/LogisticsContext';
import { OperationalTab, FontFamilyChoice } from '../../types';

export const Header: React.FC<{ onOpenSos: () => void }> = ({ onOpenSos }) => {
  const {
    tab,
    setTab,
    font,
    setFont,
    theme,
    setTheme,
    tatSeconds,
  } = useLogistics();

  const mins = Math.floor(tatSeconds / 60);
  const secs = tatSeconds % 60;
  const tatFormatted = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const tabs: { id: OperationalTab; label: string; step: string }[] = [
    { id: 'INGRESS', label: 'Ingress & Optical OCR', step: 'Phase 1' },
    { id: 'YARD_STOWAGE', label: 'Yard & 53ft Reefer', step: 'Phase 2' },
    { id: 'DOCS_EPOP', label: 'Docs & e-POP Sign', step: 'Phase 3' },
    { id: 'CONTROL_TOWER', label: 'Control Tower & Relay', step: 'Phase 4' },
  ];

  return (
    <header className="sticky top-0 z-40 border-b border-[var(--border-card)] bg-[var(--bg-card)] shadow-xs">
      {/* Top Telemetry & Control Bar */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-2.5 flex flex-wrap items-center justify-between gap-4">
        {/* Brand & Subtitle */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-amber-600 text-white flex items-center justify-center font-extrabold text-sm shadow-sm">
            NX
          </div>
          <div>
            <div className={`text-base font-extrabold tracking-tight text-[var(--text-main)] ${font === 'times' ? 'font-times' : 'font-calibri'}`}>
              NEXUS FIRST-MILE LOGISTICS
            </div>
            <div className="text-[11px] text-[var(--text-muted)] font-mono-data tracking-wide uppercase">
              Outbound Dispatch & Yard Telematics
            </div>
          </div>
        </div>

        {/* Center Live Telematics Badge */}
        <div className="hidden lg:flex items-center gap-2 px-3 py-1 rounded-full bg-[var(--bg-subtle)] border border-[var(--border-subtle)] text-xs text-[var(--text-muted)] font-mono-data">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
          <span>DAL-NORTH GATE 04 (32.7767° N, 96.7970° W) • CAN-BUS SYNCED</span>
        </div>

        {/* Controls & Metrics */}
        <div className="flex items-center gap-2.5 sm:gap-4 text-xs">
          {/* Turnaround Time (TAT) Clock */}
          <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] font-mono-data">
            <Clock className="w-3.5 h-3.5 text-amber-500" />
            <span className="text-[var(--text-muted)] hidden sm:inline">TAT CLOCK:</span>
            <span className="font-bold text-amber-600 dark:text-amber-400 text-sm">{tatFormatted}</span>
            <span className="text-[10px] text-emerald-600 dark:text-emerald-400 font-semibold">(≤25m SLA)</span>
          </div>

          {/* Typography Switcher: Calibri vs Times New Roman */}
          <div className="flex items-center bg-[var(--bg-subtle)] border border-[var(--border-card)] rounded-lg p-0.5">
            <button
              onClick={() => setFont('calibri')}
              title="Switch font to Calibri"
              className={`px-2.5 py-1 rounded text-xs font-semibold transition cursor-pointer flex items-center gap-1 ${
                font === 'calibri'
                  ? 'bg-amber-500 text-white shadow-xs'
                  : 'text-[var(--text-muted)] hover:text-[var(--text-main)]'
              }`}
            >
              <Type className="w-3 h-3" />
              <span className="font-calibri">Calibri</span>
            </button>
            <button
              onClick={() => setFont('times')}
              title="Switch font to Times New Roman"
              className={`px-2.5 py-1 rounded text-xs font-semibold transition cursor-pointer flex items-center gap-1 ${
                font === 'times'
                  ? 'bg-amber-500 text-white shadow-xs'
                  : 'text-[var(--text-muted)] hover:text-[var(--text-main)]'
              }`}
            >
              <span className="font-times text-sm font-bold leading-none">T</span>
              <span className="font-times">Times</span>
            </button>
          </div>

          {/* Clean Light / Dark Theme Switcher */}
          <button
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
            title={`Switch to ${theme === 'light' ? 'Dark' : 'Light'} Mode`}
            className="p-1.5 rounded-lg bg-[var(--bg-subtle)] border border-[var(--border-card)] text-[var(--text-muted)] hover:text-[var(--text-main)] transition cursor-pointer"
          >
            {theme === 'light' ? <Moon className="w-4 h-4" /> : <Sun className="w-4 h-4 text-amber-400" />}
          </button>

          {/* Emergency SOS Button */}
          <button
            onClick={onOpenSos}
            className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 text-red-600 dark:text-red-400 font-bold text-xs hover:bg-red-100 transition cursor-pointer"
          >
            <ShieldAlert className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">SOS</span>
          </button>
        </div>
      </div>

      {/* Clean Navigation Tabs */}
      <div className="border-t border-[var(--border-card)] bg-[var(--bg-card)]">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 flex overflow-x-auto space-x-1 sm:space-x-4 no-scrollbar">
          {tabs.map((t) => {
            const isActive = tab === t.id;
            return (
              <button
                key={t.id}
                onClick={() => setTab(t.id)}
                className={`py-3 px-3.5 border-b-2 text-xs font-bold transition flex items-center gap-2 whitespace-nowrap cursor-pointer ${
                  isActive
                    ? 'border-amber-500 text-amber-600 dark:text-amber-400 bg-[var(--primary-bg)]/40'
                    : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-main)] hover:border-[var(--border-subtle)]'
                } ${font === 'times' ? 'font-times text-sm' : 'font-calibri'}`}
              >
                <span className={`text-[10px] px-1.5 py-0.5 rounded font-mono-data ${
                  isActive ? 'bg-amber-500 text-white' : 'bg-[var(--bg-subtle)] text-[var(--text-muted)]'
                }`}>
                  {t.step}
                </span>
                <span>{t.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </header>
  );
};
