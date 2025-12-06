import React from 'react';
import { Upload, FileCode, AlertCircle } from 'lucide-react';

const FileDropZone = ({ fileName, onDrop, onFileSelect, error }) => {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <h3 className="text-sm font-semibold text-gray-700 mb-3">Proto File</h3>

      <div
        className={`
          relative group flex flex-col items-center justify-center min-h-[120px]
          p-4 rounded-md border-2 border-dashed transition-all cursor-pointer
          ${error ? 'border-red-300 bg-red-50' : 'border-gray-300 hover:border-blue-400 hover:bg-gray-50'}
        `}
        onDragOver={(e) => e.preventDefault()}
        onDrop={onDrop}
      >
        <input
          type="file"
          accept=".proto,.grpc"
          multiple
          onChange={onFileSelect}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
        />

        {fileName ? (
          <div className="flex items-center gap-2">
            <FileCode className="w-5 h-5 text-green-600" />
            <span className="text-sm font-medium text-gray-700">{fileName}</span>
          </div>
        ) : (
          <>
            <Upload className="w-6 h-6 text-gray-400 mb-2" />
            <p className="text-xs text-gray-500">Drop .proto file or click</p>
          </>
        )}
      </div>

      {error && (
        <div className="mt-2 text-xs text-red-600 flex items-center gap-1">
          <AlertCircle className="w-3 h-3" />
          {error}
        </div>
      )}
    </div>
  );
};

export default FileDropZone;