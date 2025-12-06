import React from 'react';

const RequestEditor = ({ requestBody, setRequestBody }) => {
  return (
    <div className="bg-white rounded-lg border border-gray-200 flex flex-col h-[300px]">
      <div className="bg-gray-50 border-b border-gray-200 px-4 py-2">
        <span className="text-sm font-semibold text-gray-700">Request Payload</span>
      </div>
      <textarea
        className="flex-1 w-full p-4 font-mono text-xs text-gray-800 outline-none resize-none focus:ring-2 focus:ring-blue-500"
        value={requestBody}
        onChange={(e) => setRequestBody(e.target.value)}
        spellCheck="false"
        placeholder='{\n  "message": "Hello World"\n}'
      />
    </div>
  );
};

export default RequestEditor;