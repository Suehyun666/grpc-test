import React from 'react';
import { Activity, TrendingUp, AlertTriangle, Clock, CheckCircle, XCircle } from 'lucide-react';

export default function StatisticsPanel({ stats, isRunning }) {
  if (!stats) {
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="text-sm text-gray-400 text-center">No statistics available</div>
      </div>
    );
  }

  const formatNumber = (num) => {
    if (num === undefined || num === null) return '0';
    return num.toLocaleString();
  };

  const formatLatency = (ms) => {
    if (ms === undefined || ms === null || ms === 0) return '-';
    return `${ms.toFixed(0)}ms`;
  };

  return (
    <div className="space-y-4">
      {/* Real-time Metrics */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex items-center gap-2 mb-4">
          <Activity className={`w-4 h-4 ${isRunning ? 'text-green-600' : 'text-gray-400'}`} />
          <h3 className="text-sm font-semibold text-gray-700">Real-time Metrics</h3>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="bg-blue-50 rounded-lg p-3 border border-blue-200">
            <div className="text-xs text-blue-600 font-medium mb-1">Current RPS</div>
            <div className="text-2xl font-bold text-blue-700">{formatNumber(stats.currentRps)}</div>
          </div>

          <div className="bg-purple-50 rounded-lg p-3 border border-purple-200">
            <div className="text-xs text-purple-600 font-medium mb-1">Duration</div>
            <div className="text-2xl font-bold text-purple-700">{stats.testDurationSec}s</div>
          </div>

          <div className="bg-green-50 rounded-lg p-3 border border-green-200">
            <div className="text-xs text-green-600 font-medium mb-1 flex items-center gap-1">
              <CheckCircle className="w-3 h-3" />
              Success
            </div>
            <div className="text-xl font-bold text-green-700">{formatNumber(stats.successCount)}</div>
            <div className="text-xs text-green-600">{stats.successRate?.toFixed(2)}%</div>
          </div>

          <div className="bg-red-50 rounded-lg p-3 border border-red-200">
            <div className="text-xs text-red-600 font-medium mb-1 flex items-center gap-1">
              <XCircle className="w-3 h-3" />
              Failed
            </div>
            <div className="text-xl font-bold text-red-700">{formatNumber(stats.failCount)}</div>
            <div className="text-xs text-red-600">{stats.errorRate?.toFixed(2)}%</div>
          </div>
        </div>
      </div>

      {/* Latency Statistics */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex items-center gap-2 mb-4">
          <Clock className="w-4 h-4 text-orange-600" />
          <h3 className="text-sm font-semibold text-gray-700">Latency Distribution</h3>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div className="text-center p-2 bg-gray-50 rounded border border-gray-200">
            <div className="text-xs text-gray-500 mb-1">Min</div>
            <div className="text-sm font-bold text-gray-700">{formatLatency(stats.minLatencyMs)}</div>
          </div>
          <div className="text-center p-2 bg-gray-50 rounded border border-gray-200">
            <div className="text-xs text-gray-500 mb-1">Avg</div>
            <div className="text-sm font-bold text-gray-700">{formatLatency(stats.avgLatencyMs)}</div>
          </div>
          <div className="text-center p-2 bg-gray-50 rounded border border-gray-200">
            <div className="text-xs text-gray-500 mb-1">Max</div>
            <div className="text-sm font-bold text-gray-700">{formatLatency(stats.maxLatencyMs)}</div>
          </div>
        </div>

        <div className="mt-3 grid grid-cols-3 gap-3">
          <div className="text-center p-2 bg-blue-50 rounded border border-blue-200">
            <div className="text-xs text-blue-600 mb-1">P50</div>
            <div className="text-sm font-bold text-blue-700">{formatLatency(stats.p50LatencyMs)}</div>
          </div>
          <div className="text-center p-2 bg-orange-50 rounded border border-orange-200">
            <div className="text-xs text-orange-600 mb-1">P95</div>
            <div className="text-sm font-bold text-orange-700">{formatLatency(stats.p95LatencyMs)}</div>
          </div>
          <div className="text-center p-2 bg-red-50 rounded border border-red-200">
            <div className="text-xs text-red-600 mb-1">P99</div>
            <div className="text-sm font-bold text-red-700">{formatLatency(stats.p99LatencyMs)}</div>
          </div>
        </div>
      </div>

      {/* Error Types */}
      {stats.errorTypes && Object.keys(stats.errorTypes).length > 0 && (
        <div className="bg-white rounded-lg border border-red-200 p-4">
          <div className="flex items-center gap-2 mb-3">
            <AlertTriangle className="w-4 h-4 text-red-600" />
            <h3 className="text-sm font-semibold text-red-700">Error Types</h3>
          </div>
          <div className="space-y-2">
            {Object.entries(stats.errorTypes).map(([type, count]) => (
              <div key={type} className="flex justify-between items-center text-xs">
                <span className="font-mono text-red-700">{type}</span>
                <span className="font-bold text-red-600">{formatNumber(count)}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
