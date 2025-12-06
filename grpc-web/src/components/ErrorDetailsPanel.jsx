import React from 'react';
import { AlertCircle } from 'lucide-react';

export default function ErrorDetailsPanel({ recentErrors }) {
  if (!recentErrors || recentErrors.length === 0) {
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <h3 className="text-sm font-semibold text-gray-700 mb-3">Recent Errors</h3>
        <div className="text-xs text-gray-400 text-center py-8">No errors yet</div>
      </div>
    );
  }

  const formatTimestamp = (ts) => {
    return new Date(ts).toLocaleTimeString();
  };

  return (
    <div className="bg-white rounded-lg border border-red-200 overflow-hidden">
      <div className="bg-red-50 border-b border-red-200 px-4 py-2 flex items-center gap-2">
        <AlertCircle className="w-4 h-4 text-red-600" />
        <h3 className="text-sm font-semibold text-red-700">Recent Errors (Last 100)</h3>
      </div>

      <div className="max-h-[400px] overflow-y-auto">
        <table className="w-full text-xs">
          <thead className="bg-gray-50 sticky top-0">
            <tr className="border-b border-gray-200">
              <th className="px-3 py-2 text-left font-semibold text-gray-600">Req#</th>
              <th className="px-3 py-2 text-left font-semibold text-gray-600">Time</th>
              <th className="px-3 py-2 text-left font-semibold text-gray-600">Latency</th>
              <th className="px-3 py-2 text-left font-semibold text-gray-600">Type</th>
              <th className="px-3 py-2 text-left font-semibold text-gray-600">Message</th>
            </tr>
          </thead>
          <tbody>
            {[...recentErrors].reverse().map((error, idx) => (
              <tr key={idx} className="border-b border-gray-100 hover:bg-red-50">
                <td className="px-3 py-2 font-mono text-gray-600">#{error.requestId}</td>
                <td className="px-3 py-2 text-gray-500">{formatTimestamp(error.timestamp)}</td>
                <td className="px-3 py-2 font-mono text-orange-600">{error.latencyMs}ms</td>
                <td className="px-3 py-2">
                  <span className="px-2 py-1 bg-red-100 text-red-700 rounded text-xs font-mono">
                    {error.errorType}
                  </span>
                </td>
                <td className="px-3 py-2 text-gray-700 max-w-xs truncate" title={error.errorMessage}>
                  {error.errorMessage}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
