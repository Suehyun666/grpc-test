import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

export default function LatencyChart({ data }) {
  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <h3 className="text-lg font-bold card-title mb-4">Response Time</h3>
      <ResponsiveContainer width="100%" height={250}>
        <LineChart data={data}>
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
            label={{ value: 'Latency (ms)', angle: -90, position: 'insideLeft', style: { fontSize: '11px' } }}
          />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="latency" stroke="rgb(0, 125, 255)" name="Avg (ms)" strokeWidth={2} dot={false} />
          <Line type="monotone" dataKey="p95" stroke="rgb(0, 229, 255)" name="P95 (ms)" strokeWidth={2} dot={false} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
