import React from 'react';
import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom';
import Layout from './commons/Layout';
import SimulationsPage from './pages/SimulationsPage';
import TestConfigPage from './pages/TestConfigPage';
import DashboardPage from './pages/DashboardPage';
import { useTestConfig } from './hooks/useTestConfig';
import { useSimulations } from './hooks/useSimulations';

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

function AppContent() {
  const navigate = useNavigate();
  const testConfig = useTestConfig();
  const { simulations, deleteSimulation, refreshSimulations } = useSimulations();

  const loadSimulation = (sim) => {
    testConfig.setEndpoint(sim.scenario.endpoint);
    testConfig.setSelectedService(sim.scenario.serviceName);
    testConfig.setSelectedMethod(sim.scenario.methodName);
    if (sim.scenario.loadProfile) {
      testConfig.setMode(sim.scenario.loadProfile.mode);
      testConfig.setVusers(sim.scenario.loadProfile.virtualUsers);
      testConfig.setDuration(sim.scenario.loadProfile.durationSec);
      testConfig.setRps(sim.scenario.loadProfile.targetRps);
    }
    navigate('/');
  };

  const runSimulation = async (sim) => {
    loadSimulation(sim);
    navigate('/dashboard');
    setTimeout(() => {
      testConfig.startTest();
    }, 100);
  };

  const handleSaveSimulation = async () => {
    await testConfig.saveSimulation();
    await refreshSimulations();
  };

  return (
    <Layout>
      <Routes>
        <Route path="/" element={
          <TestConfigPage
            {...testConfig}
            saveSimulation={handleSaveSimulation}
          />
        } />
        <Route path="/simulations" element={
          <SimulationsPage
            simulations={simulations}
            onLoad={loadSimulation}
            onRun={runSimulation}
            onDelete={deleteSimulation}
            onNew={() => navigate('/')}
          />
        } />
        <Route path="/dashboard" element={
          <DashboardPage
            stats={testConfig.stats}
            running={testConfig.running}
            onStop={testConfig.stopTest}
          />
        } />
      </Routes>
    </Layout>
  );
}
