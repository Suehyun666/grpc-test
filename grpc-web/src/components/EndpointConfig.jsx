import React from 'react';

const EndpointConfig = ({
  services,
  selectedService,
  selectedMethod,
  methods,
  endpoint,
  useTls,
  onServiceChange,
  onMethodChange,
  onEndpointChange,
  onTlsChange
}) => {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <h3 className="text-sm font-semibold text-gray-700 mb-3">Endpoint Configuration</h3>

      <div className="space-y-3">
        {/* Endpoint Input */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Endpoint
          </label>
          <input
            type="text"
            value={endpoint}
            onChange={(e) => onEndpointChange(e.target.value)}
            placeholder="localhost:8080"
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* TLS Checkbox */}
        <div className="flex items-center gap-2">
          <input
            type="checkbox"
            id="useTls"
            checked={useTls}
            onChange={(e) => onTlsChange(e.target.checked)}
            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <label htmlFor="useTls" className="text-xs font-medium text-gray-600">
            Use TLS
          </label>
        </div>

        {/* Service Selector */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Service
          </label>
          <select
            value={selectedService}
            onChange={onServiceChange}
            disabled={services.length === 0}
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <option value="">Select Service...</option>
            {services.map((s, idx) => (
              <option key={idx} value={s.name}>{s.name}</option>
            ))}
          </select>
        </div>

        {/* Method Selector */}
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">
            Method
          </label>
          <select
            value={selectedMethod}
            onChange={(e) => onMethodChange(e.target.value)}
            disabled={methods.length === 0}
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <option value="">Select Method...</option>
            {methods.map((m, idx) => (
              <option key={idx} value={m}>{m}</option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
};

export default EndpointConfig;