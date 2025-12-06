import React from 'react';

export default function LoadProfileConfig({
  loadMode,
  virtualUsers,
  durationSec,
  targetRps,
  workerThreads,
  onLoadModeChange,
  onVirtualUsersChange,
  onDurationChange,
  onTargetRpsChange,
  onWorkerThreadsChange
}) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <h3 className="text-sm font-semibold text-gray-700 mb-3">Load Profile</h3>

      <div className="space-y-3">
        {/* Mode Selection */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Mode
          </label>
          <select
            value={loadMode}
            onChange={(e) => onLoadModeChange(e.target.value)}
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="SINGLE">Single Request</option>
            <option value="CONSTANT_THROUGHPUT">Constant Throughput</option>
            <option value="RAMP_UP">Ramp Up</option>
            <option value="BURST">Burst</option>
          </select>
        </div>

        {/* Virtual Users */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Virtual Users
          </label>
          <input
            type="number"
            value={virtualUsers}
            onChange={(e) => onVirtualUsersChange(parseInt(e.target.value) || 1)}
            min="1"
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Duration */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Duration (seconds)
          </label>
          <input
            type="number"
            value={durationSec}
            onChange={(e) => onDurationChange(parseInt(e.target.value) || 60)}
            min="1"
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Target RPS */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Target RPS
          </label>
          <input
            type="number"
            value={targetRps}
            onChange={(e) => onTargetRpsChange(parseInt(e.target.value) || 10)}
            min="1"
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Worker Threads */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Worker Threads
          </label>
          <input
            type="number"
            value={workerThreads}
            onChange={(e) => onWorkerThreadsChange(parseInt(e.target.value) || 4)}
            min="1"
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
    </div>
  );
}
