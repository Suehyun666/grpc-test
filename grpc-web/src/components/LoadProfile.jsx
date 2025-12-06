import React from 'react';

export default function LoadProfile({
  serverConfig,
  mode,
  setMode,
  vusers,
  setVusers,
  duration,
  setDuration,
  rps,
  setRps,
  timeout,
  setTimeout,
  workerThreads,
  setWorkerThreads
}) {
  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <h3 className="text-lg font-semibold mb-6 card-title border-b pb-3">Load Profile</h3>

      <div className="space-y-5">
        {/* 1. 모드 선택 */}
        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">Mode</span>
          <select
            value={mode}
            onChange={(e) => setMode(e.target.value)}
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            {serverConfig?.supportedModes?.map(m => (
              <option key={m.value} value={m.value}>{m.label}</option>
            )) || (
              <>
                <option value="SINGLE">Single Request</option>
                <option value="BURST">Burst Mode</option>
                <option value="CONSTANT_THROUGHPUT">Constant Throughput</option>
                <option value="MAX_THROUGHPUT">Max Throughput</option>
                <option value="LOAD_TEST">Load Test</option>
              </>
            )}
          </select>
        </label>

        {/* 2. BURST 모드 설정 (가상 유저 + 총 요청 수) */}
        {mode === 'BURST' && (
          <div className="grid grid-cols-2 gap-4">
            {/* Burst Mode에서도 Virtual User 설정 필요 (연결 수 제어) */}
            <label className="block">
              <span className="text-sm font-medium text-muted mb-2 block">Virtual Users (Connections)</span>
              <input
                type="number"
                value={vusers}
                onChange={(e) => setVusers(parseInt(e.target.value) || 1)}
                className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              />
            </label>

            <label className="block">
              <span className="text-sm font-medium text-muted mb-2 block">Burst Size (Total Requests)</span>
              <input
                type="number"
                value={rps} // 백엔드의 targetRps 재사용
                onChange={(e) => setRps(parseInt(e.target.value) || 0)}
                placeholder="e.g. 1000"
                className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              />
            </label>
          </div>
        )}

        {/* 3. 일반 부하 테스트 UI (Single X, Burst X) */}
        {mode !== 'SINGLE' && mode !== 'BURST' && (
          <>
            <div className="grid grid-cols-2 gap-4">
              <label className="block">
                <span className="text-sm font-medium text-muted mb-2 block">Virtual Users</span>
                <input
                  type="number"
                  value={vusers}
                  onChange={(e) => setVusers(parseInt(e.target.value) || 1)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </label>
              <label className="block">
                <span className="text-sm font-medium text-muted mb-2 block">Duration (s)</span>
                <input
                  type="number"
                  value={duration}
                  onChange={(e) => setDuration(parseInt(e.target.value) || 0)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </label>
            </div>

            {mode !== 'MAX_THROUGHPUT' && (
              <label className="block">
                <span className="text-sm font-medium text-muted mb-2 block">Target RPS</span>
                <input
                  type="number"
                  value={rps}
                  onChange={(e) => setRps(parseInt(e.target.value) || 0)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </label>
            )}
          </>
        )}

        {/* 4. 공통 Timeout */}
        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">Timeout (s)</span>
          <input
            type="number"
            value={timeout}
            onChange={(e) => setTimeout(parseInt(e.target.value) || 0)}
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          />
        </label>

        {/* 5. Worker Threads */}
        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">
            Worker Threads (0 = auto)
          </span>
          <input
            type="number"
            value={workerThreads}
            onChange={(e) => setWorkerThreads(parseInt(e.target.value) || 0)}
            min="0"
            max="128"
            placeholder="0 for auto-calculate based on vusers"
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          />
          <span className="text-xs text-gray-500 mt-1 block">
            Auto: min(vusers, 32), max 4. Set to 0 for automatic calculation.
          </span>
        </label>
      </div>
    </div>
  );
}