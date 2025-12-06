import React from 'react';
import { Activity } from 'lucide-react';

export default function PayloadFields({ fields, setFields, serverConfig }) {
  if (fields.length === 0) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 dark:bg-slate-800 mb-4">
            <Activity className="w-8 h-8 text-gray-400 dark:text-slate-500" />
          </div>
          <p className="text-gray-500 dark:text-slate-400">Upload a proto file to configure payload fields</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {fields.map((f, i) => (
        <div key={i} className="border border-gray-200 dark:border-slate-700 rounded-lg p-5 bg-gray-50 dark:bg-slate-800 hover:bg-gray-100 dark:hover:bg-slate-700 transition-colors">
          <div className="flex items-center gap-4">
            <div className="font-mono text-sm font-bold text-gray-800 dark:text-slate-200 min-w-[140px]">{f.name}</div>
            <select
              value={f.mode}
              onChange={(e) => {
                const newFields = [...fields];
                newFields[i].mode = e.target.value;
                setFields(newFields);
              }}
              className="px-4 py-2 border border-gray-200 dark:border-slate-600 dark:bg-slate-700 dark:text-slate-100 rounded-lg text-sm font-medium focus:outline-none focus:ring-2 focus:ring-primary"
            >
              {serverConfig?.supportedFieldTypes?.map(ft => (
                <option key={ft.type} value={ft.type.toLowerCase()}>{ft.label}</option>
              )) || (
                <>
                  <option value="fixed">Fixed Value</option>
                  <option value="random_int">Random Int</option>
                  <option value="uuid">UUID</option>
                  <option value="sequence">Sequence</option>
                  <option value="round_robin">Round Robin</option>
                </>
              )}
            </select>

            {f.mode === 'fixed' && (
              <input
                type="text"
                value={f.value}
                onChange={(e) => {
                  const newFields = [...fields];
                  newFields[i].value = e.target.value;
                  setFields(newFields);
                }}
                placeholder="Enter value"
                className="flex-1 px-4 py-2 border border-gray-200 dark:border-slate-600 dark:bg-slate-700 dark:text-slate-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            )}

            {(f.mode === 'random_int' || f.mode === 'sequence' || f.mode === 'round_robin') && (
              <>
                <input
                  type="number"
                  value={f.min}
                  onChange={(e) => {
                    const newFields = [...fields];
                    newFields[i].min = parseInt(e.target.value);
                    setFields(newFields);
                  }}
                  placeholder={f.mode === 'round_robin' ? "Start (e.g., 1000)" : "Min"}
                  className="w-28 px-3 py-2 border border-gray-200 dark:border-slate-600 dark:bg-slate-700 dark:text-slate-100 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
                <input
                  type="number"
                  value={f.max}
                  onChange={(e) => {
                    const newFields = [...fields];
                    newFields[i].max = parseInt(e.target.value);
                    setFields(newFields);
                  }}
                  placeholder={f.mode === 'round_robin' ? "End (e.g., 2000)" : "Max"}
                  className="w-28 px-3 py-2 border border-gray-200 dark:border-slate-600 dark:bg-slate-700 dark:text-slate-100 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
                {f.mode === 'round_robin' && (
                  <span className="text-xs text-gray-500 dark:text-slate-400">
                    ({((f.max || 2000) - (f.min || 1000) + 1)} values)
                  </span>
                )}
              </>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
