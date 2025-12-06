import React from 'react';
import { Activity, StopCircle, TrendingUp, AlertCircle } from 'lucide-react';
import RpsChart from '../components/RpsChart';
import LatencyChart from '../components/LatencyChart';
import StatusChart from '../components/StatusChart';

export default function DashboardPage({ stats, running, onStop, chartData }) {

  if (!stats) {
    return (
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <div className="text-center">
            <div className="inline-flex items-center justify-center w-24 h-24 rounded-full bg-gray-100 mb-6">
              <Activity className="w-12 h-12 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold card-title mb-2">No active test</h3>
            <p className="text-gray-500">Start a test to see real-time metrics and statistics</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h2 className="text-3xl font-bold card-title">Live Dashboard</h2>
          <p className="text-gray-500 mt-1">Real-time performance metrics</p>
        </div>
        {running && (
          <button onClick={onStop}
            className="px-6 py-3 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white rounded-lg font-semibold shadow-lg transition-all transform hover:scale-105 flex items-center gap-2">
            <StopCircle className="w-5 h-5" /> Stop Test
          </button>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 mb-8">
        <div className="bg-gradient-to-br from-green-500 to-green-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">SUCCESS REQUESTS</div>
          <div className="text-4xl font-bold mb-1">{stats.successCount || 0}</div>
          <div className="text-sm opacity-80">Success Rate: {(stats.successRate || 0).toFixed(2)}%</div>
        </div>

        <div className="bg-gradient-to-br from-red-500 to-red-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">FAILED REQUESTS</div>
          <div className="text-4xl font-bold mb-1">{stats.failCount || 0}</div>
          <div className="text-sm opacity-80">Error Rate: {(stats.errorRate || 0).toFixed(2)}%</div>
        </div>

        <div className="bg-brand text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">CURRENT RPS</div>
          <div className="text-4xl font-bold mb-1">{stats.currentRps || 0}</div>
          <div className="text-sm opacity-80">Requests per second</div>
        </div>

        <div className="bg-gradient-to-br from-indigo-500 to-purple-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">TEST DURATION</div>
          <div className="text-4xl font-bold mb-1">{stats.testDurationSec || 0}s</div>
          <div className="text-sm opacity-80">Elapsed time</div>
        </div>
      </div>

      <div className="card rounded-xl shadow-sm border border-gray-100 p-6 mb-8">
        <div className="flex items-center gap-3 mb-6 pb-4 border-b">
          <TrendingUp className="w-6 h-6 text-secondary" />
          <h3 className="text-xl font-bold card-title">Response Time Percentiles</h3>
        </div>
        <div className="grid grid-cols-5 gap-4">
          <div className="text-center p-6 bg-gray-50 rounded-xl border border-gray-100">
            <div className="text-sm font-medium text-gray-500 mb-2">Minimum</div>
            <div className="text-3xl font-bold card-title">{stats.minLatencyMs || 0}</div>
            <div className="text-xs text-gray-400 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-primary/10 rounded-xl border border-primary/30">
            <div className="text-sm font-medium text-primary mb-2">50th Percentile</div>
            <div className="text-3xl font-bold text-primary">{stats.p50LatencyMs || 0}</div>
            <div className="text-xs text-primary/80 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-secondary/10 rounded-xl border border-secondary/30">
            <div className="text-sm font-medium text-secondary mb-2">95th Percentile</div>
            <div className="text-3xl font-bold text-secondary">{stats.p95LatencyMs || 0}</div>
            <div className="text-xs text-secondary/80 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-red-50 rounded-xl border border-red-200">
            <div className="text-sm font-medium text-red-600 mb-2">99th Percentile</div>
            <div className="text-3xl font-bold text-red-700">{stats.p99LatencyMs || 0}</div>
            <div className="text-xs text-red-500 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-gray-50 rounded-xl border border-gray-100">
            <div className="text-sm font-medium text-gray-500 mb-2">Maximum</div>
            <div className="text-3xl font-bold card-title">{stats.maxLatencyMs || 0}</div>
            <div className="text-xs text-gray-400 mt-1">ms</div>
          </div>
        </div>
      </div>

      {/* Real-time Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <RpsChart data={chartData} />
        <LatencyChart data={chartData} />
      </div>

      <StatusChart data={chartData} />

      {stats.recentErrors && stats.recentErrors.length > 0 && (
        <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex items-center gap-3 mb-6 pb-4 border-b">
            <AlertCircle className="w-6 h-6 text-red-500" />
            <h3 className="text-xl font-bold card-title">Recent Errors</h3>
            <span className="ml-auto px-3 py-1 bg-red-100 text-red-700 text-sm font-semibold rounded-full">
              {stats.recentErrors.length} errors
            </span>
          </div>
          <div className="space-y-3 max-h-[500px] overflow-y-auto">
            {[...stats.recentErrors].reverse().slice(0, 20).map((err, i) => (
              <div key={i} className="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors">
                <div className="flex justify-between items-start mb-2">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-sm font-bold text-red-700">#{err.requestId}</span>
                    <span className="px-2 py-0.5 bg-red-200 text-red-800 text-xs font-semibold rounded">
                      {err.errorType}
                    </span>
                  </div>
                  <span className="text-red-600 font-semibold text-sm">{err.latencyMs}ms</span>
                </div>
                <div className="text-gray-700 text-sm font-mono bg-white px-3 py-2 rounded border border-red-100">
                  {err.errorMessage}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
