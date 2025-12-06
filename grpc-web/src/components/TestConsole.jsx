import React from 'react';
import { CheckCircle, XCircle, Clock, Activity } from 'lucide-react';

export default function TestConsole({ stats, running }) {
  if (!stats && !running) {
    return (
      <div className="bg-gray-900 rounded-lg p-6 h-64 flex items-center justify-center">
        <p className="text-gray-500">Test results will appear here</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-900 rounded-lg p-6 font-mono text-sm">
      <div className="flex items-center gap-2 mb-4 pb-3 border-b border-gray-700">
        {running ? (
          <>
            <Activity className="w-4 h-4 text-green-400 animate-pulse" />
            <span className="text-green-400 font-semibold">Test Running...</span>
          </>
        ) : (
          <>
            <CheckCircle className="w-4 h-4 text-secondary" />
            <span className="text-secondary font-semibold">Test Completed</span>
          </>
        )}
      </div>

      {stats && (
        <div className="space-y-2 text-gray-300">
          <div className="flex justify-between">
            <span className="text-gray-500">Total Requests:</span>
            <span className="text-white font-semibold">{stats.totalRequests || 0}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Success:</span>
            <span className="text-green-400 font-semibold flex items-center gap-1">
              <CheckCircle className="w-3 h-3" />
              {stats.successCount || 0}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Failed:</span>
            <span className="text-red-400 font-semibold flex items-center gap-1">
              <XCircle className="w-3 h-3" />
              {stats.failCount || 0}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Duration:</span>
            <span className="text-white font-semibold flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {stats.testDurationSec || 0}s
            </span>
          </div>

          <div className="pt-3 mt-3 border-t border-gray-700">
            <div className="text-gray-500 mb-2">Latency:</div>
            <div className="pl-4 space-y-1">
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Min:</span>
                <span className="text-gray-300">{stats.minLatencyMs || 0}ms</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Avg:</span>
                <span className="text-gray-300">{Math.round(stats.avgLatencyMs || 0)}ms</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">P50:</span>
                <span className="text-gray-300">{stats.p50LatencyMs || 0}ms</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">P95:</span>
                <span className="text-gray-300">{stats.p95LatencyMs || 0}ms</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Max:</span>
                <span className="text-gray-300">{stats.maxLatencyMs || 0}ms</span>
              </div>
            </div>
          </div>

          {stats.currentRps > 0 && (
            <div className="flex justify-between pt-2">
              <span className="text-gray-500">Current RPS:</span>
              <span className="text-yellow-400 font-semibold">{stats.currentRps}</span>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
