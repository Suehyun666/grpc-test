import React from 'react';
import { Play, Trash2, Clock, BarChart3 } from 'lucide-react';

export default function SimulationList({ simulations, onRun, onDelete, onSelect }) {
  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString();
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200">
      <div className="bg-gray-50 border-b border-gray-200 px-4 py-3">
        <h3 className="text-sm font-semibold text-gray-700">Saved Simulations</h3>
      </div>

      {simulations.length === 0 ? (
        <div className="p-8 text-center text-gray-400 text-sm">
          No simulations yet. Create your first test scenario.
        </div>
      ) : (
        <div className="divide-y divide-gray-100">
          {simulations.map((sim) => (
            <div
              key={sim.id}
              className="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
              onClick={() => onSelect(sim)}
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <BarChart3 className="w-4 h-4 text-blue-600" />
                    <h4 className="font-semibold text-gray-800">{sim.title}</h4>
                  </div>
                  {sim.description && (
                    <p className="text-xs text-gray-500 mb-2">{sim.description}</p>
                  )}
                  <div className="flex items-center gap-4 text-xs text-gray-400">
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      Created: {formatDate(sim.createdAt)}
                    </span>
                    <span>Runs: {sim.runCount || 0}</span>
                  </div>
                </div>

                <div className="flex gap-2 ml-4">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onRun(sim);
                    }}
                    className="p-2 bg-green-600 hover:bg-green-700 text-white rounded transition-colors"
                    title="Run simulation"
                  >
                    <Play className="w-4 h-4" />
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onDelete(sim.id);
                    }}
                    className="p-2 bg-red-600 hover:bg-red-700 text-white rounded transition-colors"
                    title="Delete simulation"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
