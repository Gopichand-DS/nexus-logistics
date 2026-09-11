import React, { useState } from 'react';
import { LogisticsProvider, useLogistics } from './context/LogisticsContext';
import { Header } from './components/common/Header';
import { Footer } from './components/common/Footer';
import { IngressScanView } from './components/modules/IngressScanView';
import { YardStowageView } from './components/modules/YardStowageView';
import { DocsLedgerView } from './components/modules/DocsLedgerView';
import { ControlTowerModule } from './components/modules/ControlTowerModule';
import { ShieldAlert, X } from 'lucide-react';

const LogisticsAppContent: React.FC = () => {
  const { tab, font, toastMessage, clearToast, triggerSos } = useLogistics();
  const [showSosModal, setShowSosModal] = useState(false);

  const isTimes = font === 'times';

  const handleConfirmSos = () => {
    triggerSos();
    setShowSosModal(false);
  };

  return (
    <div className={`min-h-screen flex flex-col transition-colors duration-200 ${
      isTimes ? 'font-times' : 'font-calibri'
    }`}>
      {/* Top Header HUD with Telematics, Font Switcher & Theme Switcher */}
      <Header onOpenSos={() => setShowSosModal(true)} />

      {/* Main Operational Stage */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6">
        {tab === 'INGRESS' && <IngressScanView />}
        {tab === 'YARD_STOWAGE' && <YardStowageView />}
        {tab === 'DOCS_EPOP' && <DocsLedgerView />}
        {tab === 'CONTROL_TOWER' && <ControlTowerModule />}
      </main>

      {/* Global Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-50 max-w-md bg-[var(--bg-card)] border-2 border-amber-500 rounded-xl p-4 shadow-xl flex items-start justify-between gap-3 text-xs text-[var(--text-main)] animate-bounce">
          <div className="font-semibold leading-relaxed">{toastMessage}</div>
          <button
            onClick={clearToast}
            className="text-[var(--text-muted)] hover:text-[var(--text-main)] cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Emergency SOS Modal */}
      {showSosModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-[var(--bg-card)] border border-red-300 dark:border-red-900 rounded-xl max-w-md w-full p-6 space-y-4 shadow-2xl">
            <div className="flex items-center gap-3 text-red-600 dark:text-red-400">
              <ShieldAlert className="w-8 h-8 animate-pulse shrink-0" />
              <div className="text-lg font-bold">OPERATIONAL SOS BROADCAST</div>
            </div>
            <div className="text-xs text-[var(--text-main)] space-y-2 leading-relaxed">
              <p>
                Initiate emergency encrypted telematic alert to Dallas North Yard Safety, Sgt. Kowalski, and Texas Highway Patrol?
              </p>
              <p className="text-[var(--text-muted)] font-mono-data">
                Location: Gate 04 Staging Ingress (32.7767° N, 96.7970° W)
              </p>
            </div>
            <div className="flex justify-end gap-3 pt-3 border-t border-[var(--border-card)]">
              <button
                onClick={() => setShowSosModal(false)}
                className="px-4 py-2 rounded-lg bg-[var(--bg-subtle)] text-[var(--text-muted)] text-xs font-semibold hover:text-[var(--text-main)] cursor-pointer"
              >
                CANCEL
              </button>
              <button
                onClick={handleConfirmSos}
                className="px-4 py-2 rounded-lg bg-red-600 hover:bg-red-700 text-white font-bold text-xs shadow-xs cursor-pointer"
              >
                BROADCAST SOS ALERT
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Clean Enterprise Footer */}
      <Footer />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <LogisticsProvider>
      <LogisticsAppContent />
    </LogisticsProvider>
  );
};
