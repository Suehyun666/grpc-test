import React from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

export default function StatusChart({ data }) {
  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <h3 className="text-lg font-bold card-title mb-4">Request Status</h3>
      <ResponsiveContainer width="100%" height={250}>
        <AreaChart data={data}>
          <defs>
            <linearGradient id="colorSuccess" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#10b981" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="#10b981" stopOpacity={0.1}/>
            </linearGradient>
            <linearGradient id="colorFail" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#ef4444" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="#ef4444" stopOpacity={0.1}/>
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis
            dataKey="time"
            stroke="#999"
            style={{ fontSize: '12px' }}
            label={{ value: 'Time (seconds)', position: 'insideBottom', offset: -5, style: { fontSize: '11px' } }}
          />
          <YAxis
            stroke="#999"
            style={{ fontSize: '12px' }}
            label={{ value: 'Requests', angle: -90, position: 'insideLeft', style: { fontSize: '11px' } }}
          />
          <Tooltip />
          <Legend />
          <Area type="monotone" dataKey="success" stroke="#10b981" fillOpacity={1} fill="url(#colorSuccess)" name="Success" />
          <Area type="monotone" dataKey="fail" stroke="#ef4444" fillOpacity={1} fill="url(#colorFail)" name="Failed" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
