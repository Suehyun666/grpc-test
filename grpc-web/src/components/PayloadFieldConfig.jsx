import React, { useState } from 'react';
import { Plus, Trash2, Settings } from 'lucide-react';

export default function PayloadFieldConfig({ fields, onChange }) {
  const [showAddField, setShowAddField] = useState(false);
  const [newFieldName, setNewFieldName] = useState('');
  const [newFieldType, setNewFieldType] = useState('FIXED');
  const [newFieldValue, setNewFieldValue] = useState('');

  const addField = () => {
    if (!newFieldName) return;

    const newField = {
      name: newFieldName,
      type: newFieldType,
      value: newFieldValue,
      minValue: 0,
      maxValue: 100
    };

    onChange([...fields, newField]);
    setNewFieldName('');
    setNewFieldValue('');
    setShowAddField(false);
  };

  const removeField = (index) => {
    onChange(fields.filter((_, i) => i !== index));
  };

  const updateField = (index, updates) => {
    const updated = fields.map((f, i) => i === index ? { ...f, ...updates } : f);
    onChange(updated);
  };

  return (
    <div className="bg-white dark:bg-slate-800 rounded-lg border border-gray-200 dark:border-slate-700 p-4">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <Settings className="w-4 h-4 text-gray-600 dark:text-slate-400" />
          <h3 className="text-sm font-semibold text-gray-700 dark:text-slate-200">Payload Field Rules</h3>
        </div>
        <button
          onClick={() => setShowAddField(!showAddField)}
          className="text-xs px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white rounded flex items-center gap-1"
        >
          <Plus className="w-3 h-3" /> Add Field
        </button>
      </div>

      {/* Add Field Form */}
      {showAddField && (
        <div className="mb-3 p-3 bg-gray-50 dark:bg-slate-700 rounded border border-gray-200 dark:border-slate-600 space-y-2">
          <input
            type="text"
            value={newFieldName}
            onChange={(e) => setNewFieldName(e.target.value)}
            placeholder="Field name (e.g., account_id)"
            className="w-full px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
          />
          <select
            value={newFieldType}
            onChange={(e) => setNewFieldType(e.target.value)}
            className="w-full px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
          >
            <option value="FIXED">Fixed Value</option>
            <option value="RANDOM_INT">Random Integer</option>
            <option value="RANDOM_STRING">Random String</option>
            <option value="UUID">UUID</option>
            <option value="SEQUENCE">Sequence (1, 2, 3...)</option>
            <option value="ROUND_ROBIN">Round Robin (균등 분배)</option>
          </select>
          {newFieldType === 'FIXED' && (
            <input
              type="text"
              value={newFieldValue}
              onChange={(e) => setNewFieldValue(e.target.value)}
              placeholder="Value"
              className="w-full px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
            />
          )}
          <div className="flex gap-2">
            <button
              onClick={addField}
              className="flex-1 px-3 py-1 bg-green-600 text-white text-xs rounded"
            >
              Add
            </button>
            <button
              onClick={() => setShowAddField(false)}
              className="flex-1 px-3 py-1 bg-gray-300 dark:bg-slate-600 text-gray-700 dark:text-slate-200 text-xs rounded"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      {/* Field List */}
      <div className="space-y-2">
        {fields.length === 0 ? (
          <div className="text-xs text-gray-400 dark:text-slate-500 text-center py-4">
            No field rules configured. Add rules to customize payload generation.
          </div>
        ) : (
          fields.map((field, index) => (
            <div key={index} className="p-2 bg-gray-50 dark:bg-slate-700 rounded border border-gray-200 dark:border-slate-600">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-mono text-xs font-semibold text-gray-700 dark:text-slate-200">
                      {field.name}
                    </span>
                    <span className="px-2 py-0.5 bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300 text-xs rounded">
                      {field.type}
                    </span>
                  </div>

                  {/* Type-specific configuration */}
                  {field.type === 'FIXED' && (
                    <input
                      type="text"
                      value={field.value || ''}
                      onChange={(e) => updateField(index, { value: e.target.value })}
                      placeholder="Fixed value"
                      className="w-full px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded mt-1"
                    />
                  )}

                  {field.type === 'RANDOM_INT' && (
                    <div className="flex gap-2 mt-1">
                      <input
                        type="number"
                        value={field.minValue || 0}
                        onChange={(e) => updateField(index, { minValue: parseInt(e.target.value) })}
                        placeholder="Min"
                        className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                      />
                      <input
                        type="number"
                        value={field.maxValue || 100}
                        onChange={(e) => updateField(index, { maxValue: parseInt(e.target.value) })}
                        placeholder="Max"
                        className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                      />
                    </div>
                  )}

                  {field.type === 'SEQUENCE' && (
                    <div className="flex gap-2 mt-1">
                      <input
                        type="number"
                        value={field.startValue || 1}
                        onChange={(e) => updateField(index, { startValue: parseInt(e.target.value) })}
                        placeholder="Start"
                        className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                      />
                      <input
                        type="number"
                        value={field.endValue || 1000}
                        onChange={(e) => updateField(index, { endValue: parseInt(e.target.value) })}
                        placeholder="End"
                        className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                      />
                    </div>
                  )}

                  {field.type === 'ROUND_ROBIN' && (
                    <div className="space-y-1 mt-1">
                      <div className="flex gap-2">
                        <input
                          type="number"
                          value={field.minValue || 1000}
                          onChange={(e) => updateField(index, { minValue: parseInt(e.target.value) })}
                          placeholder="Start (e.g., 1000)"
                          className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                        />
                        <input
                          type="number"
                          value={field.maxValue || 2000}
                          onChange={(e) => updateField(index, { maxValue: parseInt(e.target.value) })}
                          placeholder="End (e.g., 2000)"
                          className="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-slate-600 dark:bg-slate-600 dark:text-slate-100 rounded"
                        />
                      </div>
                      <div className="text-xs text-gray-500 dark:text-slate-400">
                        Distributes {(field.maxValue || 2000) - (field.minValue || 1000) + 1} values evenly across vusers
                      </div>
                    </div>
                  )}
                </div>

                <button
                  onClick={() => removeField(index)}
                  className="ml-2 p-1 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded"
                >
                  <Trash2 className="w-3 h-3" />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
