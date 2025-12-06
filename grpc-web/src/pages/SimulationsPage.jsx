import React from 'react';
import { Activity } from 'lucide-react';

export default function SimulationsPage({ simulations, onLoad, onDelete, onNew }) {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h2 className="text-3xl font-bold card-title">Simulations</h2>
          <p className="text-gray-500 mt-1">Manage your test scenarios</p>
        </div>
        <button onClick={onNew}
          className="px-6 py-3 bg-brand hover:bg-brand/90 text-white rounded-lg font-semibold shadow-lg transition-all transform hover:scale-105">
          + New Simulation
        </button>
      </div>

      {simulations.length === 0 ? (
        <div className="card rounded-xl shadow-sm border border-gray-100 p-16 text-center">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gray-100 mb-6">
            <Activity className="w-10 h-10 text-gray-400" />
          </div>
          <h3 className="text-xl font-semibold card-title mb-2">No simulations yet</h3>
          <p className="text-gray-500">Create your first test scenario to get started</p>
        </div>
      ) : (
        <div className="grid gap-4">
          {simulations.map(sim => (
            <div key={sim.id} className="card rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="text-xl font-bold card-title">{sim.title}</h3>
                    <span className="px-2 py-1 bg-blue-100 text-blue-700 text-xs font-medium rounded">
                      {sim.scenario?.loadProfile?.mode || 'N/A'}
                    </span>
                  </div>
                  <p className="text-gray-600 font-mono text-sm mb-3">
                    {sim.scenario?.serviceName}/{sim.scenario?.methodName}
                  </p>
                  <div className="flex gap-6 text-sm text-gray-500">
                    <div className="flex items-center gap-2">
                      <span className="font-medium">Endpoint:</span>
                      <span className="font-mono">{sim.scenario?.endpoint}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-medium">Created:</span>
                      <span>{new Date(sim.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                </div>
                <div className="flex gap-3 ml-4">
                  <button onClick={() => onLoad(sim)}
                    className="px-5 py-2.5 bg-green-500 hover:bg-green-600 text-white rounded-lg font-medium transition-colors shadow-sm">
                    Run
                  </button>
                  <button onClick={() => onDelete(sim.id)}
                    className="px-5 py-2.5 bg-red-500 hover:bg-red-600 text-white rounded-lg font-medium transition-colors shadow-sm">
                    Delete
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
