import React, { useState } from 'react';
import FileDropZone from './FileDropZone';

export default function ServiceConfig({
  onProtoUpload,
  endpoint,
  setEndpoint,
  services,
  selectedService,
  setSelectedService,
  methods,
  setMethods,
  selectedMethod,
  onMethodChange
}) {
  const [fileName, setFileName] = useState('');
  const [uploadError, setUploadError] = useState('');

  const handleFiles = (files) => {
    if (!files || files.length === 0) return;

    setUploadError('');
    const protoFiles = Array.from(files).filter(f =>
      f.name.endsWith('.proto') || f.name.endsWith('.grpc')
    );

    if (protoFiles.length === 0) {
      setUploadError('Please upload .proto or .grpc files');
      return;
    }

    setFileName(protoFiles.map(f => f.name).join(', '));
    onProtoUpload(protoFiles);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    handleFiles(e.dataTransfer.files);
  };

  const handleFileSelect = (e) => {
    handleFiles(e.target.files);
  };

  return (
    <div className="card rounded-xl shadow-sm p-6">
      <h3 className="card-title text-lg font-semibold mb-6 border-b pb-3" style={{ borderColor: 'var(--border-color)' }}>Service Configuration</h3>

      <div className="space-y-5">
        <FileDropZone
          fileName={fileName}
          onDrop={handleDrop}
          onFileSelect={handleFileSelect}
          error={uploadError}
        />

        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">Endpoint</span>
          <input
            type="text"
            value={endpoint}
            onChange={(e) => setEndpoint(e.target.value)}
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow"
          />
        </label>

        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">Service</span>
          <select
            value={selectedService}
            onChange={(e) => {
              setSelectedService(e.target.value);
              const svc = services.find(s => s.name === e.target.value);
              setMethods(svc?.methods || []);
            }}
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            {services.map(s => <option key={s.name}>{s.name}</option>)}
          </select>
        </label>

        <label className="block">
          <span className="text-sm font-medium text-muted mb-2 block">Method</span>
          <select
            value={selectedMethod}
            onChange={(e) => onMethodChange(e.target.value)}
            className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            {methods.map(m => <option key={m}>{m}</option>)}
          </select>
        </label>
      </div>
    </div>
  );
}
