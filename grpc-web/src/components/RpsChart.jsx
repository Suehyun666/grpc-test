import React from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function RpsChart({ data }) {
  // X축 범위 계산 (동적 스케일링)
  const xDomain = data.length > 0 ? [0, Math.max(...data.map(d => d.timeValue || 0))] : [0, 60];

  return (
    <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
      <h3 className="text-lg font-bold card-title mb-4">Requests Per Second</h3>
      <ResponsiveContainer width="100%" height={250}>
        <AreaChart data={data}>
          <defs>
            <linearGradient id="rpsGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="rgb(0, 125, 255)" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="rgb(0, 229, 255)" stopOpacity={0.1}/>
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
            label={{ value: 'RPS', angle: -90, position: 'insideLeft', style: { fontSize: '11px' } }}
          />
          <Tooltip />
          <Area type="monotone" dataKey="rps" stroke="rgb(0, 125, 255)" fillOpacity={1} fill="url(#rpsGradient)" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
