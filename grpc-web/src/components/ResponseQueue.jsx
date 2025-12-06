import React, { useEffect, useRef } from 'react';

const ResponseQueue = ({ stats }) => {
  const scrollRef = useRef(null);
  const responses = stats?.recentResponses || [];

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [responses]);

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('ko-KR', { hour12: false });
  };

  const formatJson = (jsonStr) => {
    try {
      const obj = JSON.parse(jsonStr);
      return JSON.stringify(obj, null, 2);
    } catch (e) {
      return jsonStr;
    }
  };

  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold card-title">Recent Responses</h3>
        <span className="px-3 py-1 bg-primary/10 text-primary text-sm font-semibold rounded-full">
          {responses.length} / 50
        </span>
      </div>
      <div
        ref={scrollRef}
        className="overflow-y-auto max-h-[400px] space-y-3 custom-scrollbar"
      >
        {responses.length === 0 ? (
          <div className="text-center py-12 text-gray-400 text-sm">
            No responses yet. Start a test to see response data.
          </div>
        ) : (
          responses.map((response, idx) => (
            <div key={idx} className="p-4 bg-gray-50 rounded-lg border border-gray-200 hover:bg-gray-100 transition-colors">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 bg-green-500 text-white text-xs font-bold rounded">
                    SUCCESS
                  </span>
                  <span className="text-gray-600 text-xs font-mono">
                    #{response.requestId}
                  </span>
                  <span className="text-gray-500 text-xs">
                    {response.serviceName}.{response.methodName}
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-green-600 font-semibold text-sm">
                    {response.latencyMs}ms
                  </span>
                  <span className="text-gray-400 text-xs">
                    {formatTimestamp(response.timestamp)}
                  </span>
                </div>
              </div>
              <pre className="bg-white p-3 rounded border border-gray-200 text-xs font-mono overflow-x-auto">
                {formatJson(response.responseData)}
              </pre>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ResponseQueue;
