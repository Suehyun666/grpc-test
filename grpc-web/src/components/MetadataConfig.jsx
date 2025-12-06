import React, { useState } from 'react';
import { Plus, X } from 'lucide-react';

export default function MetadataConfig({ metadata, setMetadata }) {
  const [entries, setEntries] = useState(
    Object.entries(metadata || {}).map(([key, value]) => ({ key, value }))
  );

  const addEntry = () => {
    setEntries([...entries, { key: '', value: '' }]);
  };

  const removeEntry = (index) => {
    const newEntries = entries.filter((_, i) => i !== index);
    setEntries(newEntries);
    updateMetadata(newEntries);
  };

  const updateEntry = (index, field, value) => {
    const newEntries = [...entries];
    newEntries[index][field] = value;
    setEntries(newEntries);
    updateMetadata(newEntries);
  };

  const updateMetadata = (newEntries) => {
    const newMetadata = {};
    newEntries.forEach(({ key, value }) => {
      if (key.trim()) {
        newMetadata[key.trim()] = value;
      }
    });
    setMetadata(newMetadata);
  };

  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-lg font-semibold card-title">gRPC Metadata (Headers)</h3>
        <button
          onClick={addEntry}
          className="px-3 py-1.5 bg-primary hover:bg-primary/90 text-white text-sm rounded-lg flex items-center gap-1 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Add
        </button>
      </div>

      {entries.length === 0 ? (
        <div className="text-center py-8 text-gray-400 text-sm">
          No metadata. Click "Add" to add headers.
        </div>
      ) : (
        <div className="space-y-3">
          {entries.map((entry, index) => (
            <div key={index} className="flex gap-2">
              <input
                type="text"
                value={entry.key}
                onChange={(e) => updateEntry(index, 'key', e.target.value)}
                placeholder="Header key (e.g., authorization)"
                className="flex-1 px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              />
              <input
                type="text"
                value={entry.value}
                onChange={(e) => updateEntry(index, 'value', e.target.value)}
                placeholder="Header value"
                className="flex-1 px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              />
              <button
                onClick={() => removeEntry(index)}
                className="p-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors"
                title="Remove"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
