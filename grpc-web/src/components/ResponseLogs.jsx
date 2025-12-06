import React, { useEffect, useRef } from 'react';

const ResponseLogs = ({ logs }) => {
  const scrollRef = useRef(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [logs]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'SUCCESS': return 'text-green-600 bg-green-50 border-green-200';
      case 'RUNNING': return 'text-blue-600 bg-blue-50 border-blue-200';
      case 'STOPPED': return 'text-orange-600 bg-orange-50 border-orange-200';
      case 'ERROR': return 'text-red-600 bg-red-50 border-red-200';
      default: return 'text-gray-600 bg-gray-50 border-gray-200';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 flex flex-col h-[350px]">
      <div className="bg-gray-50 border-b border-gray-200 px-4 py-2">
        <span className="text-sm font-semibold text-gray-700">Logs & Results</span>
      </div>
      <div
        ref={scrollRef}
        className="p-4 overflow-y-auto flex-1 space-y-2 custom-scrollbar"
      >
        {logs.length === 0 ? (
          <div className="text-gray-400 text-xs italic">No logs yet. Start a test to see results.</div>
        ) : (
          logs.map((log, idx) => (
            <div key={idx} className={`p-3 rounded border text-xs ${getStatusColor(log.status)}`}>
              <div className="flex items-center justify-between mb-1">
                <span className="font-semibold">{log.status}</span>
                <span className="text-gray-500 font-mono">{log.time}</span>
              </div>
              <div className="font-medium mb-1">{log.message}</div>
              <div className="text-xs opacity-75">{log.details}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ResponseLogs;