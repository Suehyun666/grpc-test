import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom';
import { Play, StopCircle, Save, Activity, TrendingUp, AlertCircle } from 'lucide-react';
import Layout from './commons/Layout';
import { fileService, testService, simulationService, configService } from './api';

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

function AppContent() {
  const navigate = useNavigate();
  const [simulations, setSimulations] = useState([]);

  const [serverConfig, setServerConfig] = useState(null);

  const [protoFile, setProtoFile] = useState(null);
  const [serverProtoPath, setServerProtoPath] = useState('');
  const [services, setServices] = useState([]);
  const [selectedService, setSelectedService] = useState('');
  const [methods, setMethods] = useState([]);
  const [selectedMethod, setSelectedMethod] = useState('');
  const [endpoint, setEndpoint] = useState('localhost:8080');

  const [mode, setMode] = useState('CONSTANT_THROUGHPUT');
  const [vusers, setVusers] = useState(10);
  const [duration, setDuration] = useState(60);
  const [rps, setRps] = useState(100);
  const [timeout, setTimeout] = useState(10);

  const [fields, setFields] = useState([]);

  const [running, setRunning] = useState(false);
  const [stats, setStats] = useState(null);

  useEffect(() => {
    configService.getServerConfig()
      .then(setServerConfig)
      .catch(err => console.error('Failed to load server config:', err));

    simulationService.getSimulations()
      .then(setSimulations)
      .catch(err => console.error('Failed to load simulations:', err));
  }, []);

  useEffect(() => {
    if (mode === 'SINGLE') {
      setVusers(1);
      setDuration(1);
    }
  }, [mode]);

  useEffect(() => {
    if (!running) return;
    const interval = setInterval(() => {
      testService.getDetailedStats()
        .then(setStats)
        .catch(err => console.error('Failed to get stats:', err));
    }, 1000);
    return () => clearInterval(interval);
  }, [running]);

  // Upload proto file to backend
  const handleProtoUpload = async (files) => {
    if (!files || files.length === 0) return;

    setProtoFile(files[0]);

    try {
      const protoInfo = await fileService.uploadProto(files);

      setServerProtoPath(protoInfo.path);
      console.log('Proto uploaded:', protoInfo);

      if (protoInfo.services && protoInfo.services.length > 0) {
        const firstService = protoInfo.services[0];

        // Services 정보 설정
        setServices(protoInfo.services);
        setSelectedService(firstService.name);

        if (firstService.methods && firstService.methods.length > 0) {
          const firstMethod = firstService.methods[0];

          setMethods(firstService.methods.map(m => m.name));
          setSelectedMethod(firstMethod.name);

          // 첫 번째 메서드의 필드로 초기화
          setFields(firstMethod.fields.map(f => ({
            name: f.name,
            type: f.type,
            value: '',
            mode: 'fixed',
            min: 1,
            max: 1000
          })));
        }
      }
    } catch (e) {
      console.error('Upload error:', e);
      alert('Error uploading proto file: ' + e.message);
    }
  };

  // 메서드 변경 시 해당 메서드의 필드로 업데이트
  const handleMethodChange = (methodName) => {
    setSelectedMethod(methodName);

    const currentService = services.find(s => s.name === selectedService);
    if (currentService) {
      const method = currentService.methods.find(m => m.name === methodName);
      if (method && method.fields) {
        setFields(method.fields.map(f => ({
          name: f.name,
          type: f.type,
          value: '',
          mode: 'fixed',
          min: 1,
          max: 1000
        })));
      }
    }
  };

  const startTest = async () => {
    const finalProtoPath = serverProtoPath || protoFile?.name;

    const scenario = {
      protoFilePath: finalProtoPath,
      serviceName: selectedService,
      methodName: selectedMethod,
      endpoint,
      timeoutSec: timeout,
      loadProfile: { mode, virtualUsers: vusers, durationSec: duration, targetRps: rps, workerThreads: 4 },
      fieldRules: fields.reduce((acc, f) => {
        if (f.mode === 'fixed') acc[f.name] = { type: 'FIXED', value: f.value };
        else if (f.mode === 'random') acc[f.name] = { type: 'RANDOM_INT', minValue: f.min, maxValue: f.max };
        else if (f.mode === 'uuid') acc[f.name] = { type: 'UUID' };
        else if (f.mode === 'sequence') acc[f.name] = { type: 'SEQUENCE', minValue: f.min, maxValue: f.max };
        return acc;
      }, {})
    };

    setRunning(true);
    setStats(null);
    await testService.startTest(scenario);
  };

  const stopTest = async () => {
    setRunning(false);
    await testService.stopTest();
  };

  const saveSimulation = async () => {
    const title = prompt('Simulation name:');
    if (!title) return;

    const scenario = {
      protoFilePath: protoFile?.name,
      serviceName: selectedService,
      methodName: selectedMethod,
      endpoint,
      loadProfile: { mode, virtualUsers: vusers, durationSec: duration, targetRps: rps }
    };

    await simulationService.saveSimulation(title, scenario);

    const updatedSimulations = await simulationService.getSimulations();
    setSimulations(updatedSimulations);
  };

  const loadSimulation = (sim) => {
    setEndpoint(sim.scenario.endpoint);
    setSelectedService(sim.scenario.serviceName);
    setSelectedMethod(sim.scenario.methodName);
    navigate('/test');
  };

  const deleteSimulation = async (id) => {
    await simulationService.deleteSimulation(id);
    setSimulations(simulations.filter(s => s.id !== id));
  };

  return (
    <Layout>
      <Routes>
        <Route path="/" element={
          <SimulationsView
            simulations={simulations}
            onLoad={loadSimulation}
            onDelete={deleteSimulation}
            onNew={() => navigate('/test')}
          />
        } />
        <Route path="/test" element={
          <TestConfigView
            serverConfig={serverConfig}
            protoFile={protoFile}
            onProtoUpload={handleProtoUpload}
            endpoint={endpoint}
            setEndpoint={setEndpoint}
            services={services}
            selectedService={selectedService}
            setSelectedService={setSelectedService}
            methods={methods}
            setMethods={setMethods}
            selectedMethod={selectedMethod}
            onMethodChange={handleMethodChange}
            mode={mode}
            setMode={setMode}
            vusers={vusers}
            setVusers={setVusers}
            duration={duration}
            setDuration={setDuration}
            rps={rps}
            setRps={setRps}
            timeout={timeout}
            setTimeout={setTimeout}
            fields={fields}
            setFields={setFields}
            running={running}
            startTest={startTest}
            stopTest={stopTest}
            saveSimulation={saveSimulation}
          />
        } />
        <Route path="/dashboard" element={
          <DashboardView stats={stats} running={running} onStop={stopTest} />
        } />
      </Routes>
    </Layout>
  );
}

// Simulations View
function SimulationsView({ simulations, onLoad, onDelete, onNew }) {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Simulations</h2>
          <p className="text-gray-500 mt-1">Manage your test scenarios</p>
        </div>
        <button onClick={onNew}
          className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white rounded-lg font-semibold shadow-lg transition-all transform hover:scale-105">
          + New Simulation
        </button>
      </div>

      {simulations.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-16 text-center">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gray-100 mb-6">
            <Activity className="w-10 h-10 text-gray-400" />
          </div>
          <h3 className="text-xl font-semibold text-gray-900 mb-2">No simulations yet</h3>
          <p className="text-gray-500">Create your first test scenario to get started</p>
        </div>
      ) : (
        <div className="grid gap-4">
          {simulations.map(sim => (
            <div key={sim.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="text-xl font-bold text-gray-900">{sim.title}</h3>
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

// Test Config View
function TestConfigView(props) {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-gray-900">Configure Test</h2>
        <p className="text-gray-500 mt-1">Set up your gRPC load test parameters</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Config */}
        <div className="space-y-6">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h3 className="text-lg font-semibold mb-6 text-gray-900 border-b pb-3">Service Configuration</h3>

            <div className="space-y-5">
              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Proto Files</span>
                <input type="file" accept=".proto" multiple onChange={(e) => props.onProtoUpload(Array.from(e.target.files))}
                  className="block w-full text-sm file:mr-4 file:py-2.5 file:px-4 file:rounded-lg file:border-0 file:bg-orange-50 file:text-orange-700 file:font-medium hover:file:bg-orange-100 file:cursor-pointer transition-colors" />
              </label>

              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Endpoint</span>
                <input type="text" value={props.endpoint} onChange={(e) => props.setEndpoint(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-shadow" />
              </label>

              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Service</span>
                <select value={props.selectedService} onChange={(e) => {
                  props.setSelectedService(e.target.value);
                  const svc = props.services.find(s => s.name === e.target.value);
                  props.setMethods(svc?.methods || []);
                  props.setSelectedMethod(svc?.methods[0]);
                }} className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                  {props.services.map(s => <option key={s.name}>{s.name}</option>)}
                </select>
              </label>

              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Method</span>
                <select value={props.selectedMethod} onChange={(e) => props.onMethodChange(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                  {props.methods.map(m => <option key={m}>{m}</option>)}
                </select>
              </label>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h3 className="text-lg font-semibold mb-6 text-gray-900 border-b pb-3">Load Profile</h3>

            <div className="space-y-5">
              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Mode</span>
                <select value={props.mode} onChange={(e) => props.setMode(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                  <option value="SINGLE">Single Request</option>
                  <option value="CONSTANT_THROUGHPUT">Constant Throughput</option>
                  <option value="RAMP_UP">Ramp Up</option>
                </select>
              </label>

              {/* SINGLE 모드에서는 VUsers, Duration, RPS 숨김 */}
              {props.mode !== 'SINGLE' && (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <label className="block">
                      <span className="text-sm font-medium text-gray-700 mb-2 block">Virtual Users</span>
                      <input type="number" value={props.vusers} onChange={(e) => props.setVusers(parseInt(e.target.value))}
                        className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent" />
                    </label>
                    <label className="block">
                      <span className="text-sm font-medium text-gray-700 mb-2 block">Duration (s)</span>
                      <input type="number" value={props.duration} onChange={(e) => props.setDuration(parseInt(e.target.value))}
                        className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent" />
                    </label>
                  </div>

                  <label className="block">
                    <span className="text-sm font-medium text-gray-700 mb-2 block">Target RPS</span>
                    <input type="number" value={props.rps} onChange={(e) => props.setRps(parseInt(e.target.value))}
                      className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent" />
                  </label>
                </>
              )}

              <label className="block">
                <span className="text-sm font-medium text-gray-700 mb-2 block">Timeout (s)</span>
                <input type="number" value={props.timeout} onChange={(e) => props.setTimeout(parseInt(e.target.value))}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent" />
              </label>
            </div>
          </div>

          <div className="flex flex-col gap-3">
            <button onClick={props.startTest} disabled={props.running || !props.selectedMethod}
              className="w-full py-3.5 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white rounded-lg font-semibold disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 shadow-lg transition-all">
              <Play className="w-5 h-5" /> Start Test
            </button>

            <button onClick={props.saveSimulation} disabled={!props.selectedMethod}
              className="w-full py-3 bg-orange-500 hover:bg-orange-600 text-white rounded-lg font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 transition-colors">
              <Save className="w-5 h-5" /> Save Simulation
            </button>
          </div>
        </div>

        {/* Right Column: Payload Fields */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 h-full">
            <h3 className="text-lg font-semibold mb-6 text-gray-900 border-b pb-3">Payload Fields</h3>

            {props.fields.length === 0 ? (
              <div className="flex items-center justify-center h-96">
                <div className="text-center">
                  <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
                    <Activity className="w-8 h-8 text-gray-400" />
                  </div>
                  <p className="text-gray-500">Upload a proto file to configure payload fields</p>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                {props.fields.map((f, i) => (
                  <div key={i} className="border border-gray-200 rounded-lg p-5 bg-gray-50 hover:bg-gray-100 transition-colors">
                    <div className="flex items-center gap-4">
                      <div className="font-mono text-sm font-bold text-gray-800 min-w-[140px]">{f.name}</div>
                      <select value={f.mode} onChange={(e) => {
                        const newFields = [...props.fields];
                        newFields[i].mode = e.target.value;
                        props.setFields(newFields);
                      }} className="px-4 py-2 border border-gray-200 rounded-lg text-sm font-medium focus:outline-none focus:ring-2 focus:ring-orange-500">
                        {props.serverConfig?.supportedFieldTypes?.map(ft => (
                          <option key={ft.type} value={ft.type.toLowerCase()}>{ft.label}</option>
                        )) || (
                          <>
                            <option value="fixed">Fixed Value</option>
                            <option value="random_int">Random Int</option>
                            <option value="uuid">UUID</option>
                            <option value="sequence">Sequence</option>
                            <option value="csv_feeder">CSV Feeder</option>
                          </>
                        )}
                      </select>

                      {f.mode === 'fixed' && (
                        <input type="text" value={f.value} onChange={(e) => {
                          const newFields = [...props.fields];
                          newFields[i].value = e.target.value;
                          props.setFields(newFields);
                        }} placeholder="Enter value" className="flex-1 px-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
                      )}

                      {(f.mode === 'random' || f.mode === 'sequence') && (
                        <div className="flex items-center gap-3 flex-1">
                          <input type="number" value={f.min} onChange={(e) => {
                            const newFields = [...props.fields];
                            newFields[i].min = parseInt(e.target.value);
                            props.setFields(newFields);
                          }} placeholder="Min" className="w-28 px-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
                          <span className="text-gray-400 font-medium">→</span>
                          <input type="number" value={f.max} onChange={(e) => {
                            const newFields = [...props.fields];
                            newFields[i].max = parseInt(e.target.value);
                            props.setFields(newFields);
                          }} placeholder="Max" className="w-28 px-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
                        </div>
                      )}

                      {f.mode === 'uuid' && (
                        <span className="text-sm text-gray-500 italic flex-1">Auto-generated UUID v4</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

// Dashboard View
function DashboardView({ stats, running, onStop }) {
  if (!stats) {
    return (
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <div className="text-center">
            <div className="inline-flex items-center justify-center w-24 h-24 rounded-full bg-gray-100 mb-6">
              <Activity className="w-12 h-12 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-2">No active test</h3>
            <p className="text-gray-500">Start a test to see real-time metrics and statistics</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Live Dashboard</h2>
          <p className="text-gray-500 mt-1">Real-time performance metrics</p>
        </div>
        {running && (
          <button onClick={onStop}
            className="px-6 py-3 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white rounded-lg font-semibold shadow-lg transition-all transform hover:scale-105 flex items-center gap-2">
            <StopCircle className="w-5 h-5" /> Stop Test
          </button>
        )}
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 mb-8">
        <div className="bg-gradient-to-br from-green-500 to-green-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">SUCCESS REQUESTS</div>
          <div className="text-4xl font-bold mb-1">{stats.successCount || 0}</div>
          <div className="text-sm opacity-80">Success Rate: {(stats.successRate || 0).toFixed(2)}%</div>
        </div>

        <div className="bg-gradient-to-br from-red-500 to-red-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">FAILED REQUESTS</div>
          <div className="text-4xl font-bold mb-1">{stats.failCount || 0}</div>
          <div className="text-sm opacity-80">Error Rate: {(stats.errorRate || 0).toFixed(2)}%</div>
        </div>

        <div className="bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">CURRENT RPS</div>
          <div className="text-4xl font-bold mb-1">{stats.currentRps || 0}</div>
          <div className="text-sm opacity-80">Requests per second</div>
        </div>

        <div className="bg-gradient-to-br from-purple-500 to-purple-600 text-white rounded-xl p-6 shadow-lg">
          <div className="text-sm font-medium opacity-90 mb-2">TEST DURATION</div>
          <div className="text-4xl font-bold mb-1">{stats.testDurationSec || 0}s</div>
          <div className="text-sm opacity-80">Elapsed time</div>
        </div>
      </div>

      {/* Latency Stats */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-8">
        <div className="flex items-center gap-3 mb-6 pb-4 border-b">
          <TrendingUp className="w-6 h-6 text-orange-500" />
          <h3 className="text-xl font-bold text-gray-900">Response Time Percentiles</h3>
        </div>
        <div className="grid grid-cols-5 gap-4">
          <div className="text-center p-6 bg-gray-50 rounded-xl border border-gray-100">
            <div className="text-sm font-medium text-gray-500 mb-2">Minimum</div>
            <div className="text-3xl font-bold text-gray-900">{stats.minLatencyMs || 0}</div>
            <div className="text-xs text-gray-400 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-blue-50 rounded-xl border border-blue-200">
            <div className="text-sm font-medium text-blue-600 mb-2">50th Percentile</div>
            <div className="text-3xl font-bold text-blue-700">{stats.p50LatencyMs || 0}</div>
            <div className="text-xs text-blue-500 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-orange-50 rounded-xl border border-orange-200">
            <div className="text-sm font-medium text-orange-600 mb-2">95th Percentile</div>
            <div className="text-3xl font-bold text-orange-700">{stats.p95LatencyMs || 0}</div>
            <div className="text-xs text-orange-500 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-red-50 rounded-xl border border-red-200">
            <div className="text-sm font-medium text-red-600 mb-2">99th Percentile</div>
            <div className="text-3xl font-bold text-red-700">{stats.p99LatencyMs || 0}</div>
            <div className="text-xs text-red-500 mt-1">ms</div>
          </div>
          <div className="text-center p-6 bg-gray-50 rounded-xl border border-gray-100">
            <div className="text-sm font-medium text-gray-500 mb-2">Maximum</div>
            <div className="text-3xl font-bold text-gray-900">{stats.maxLatencyMs || 0}</div>
            <div className="text-xs text-gray-400 mt-1">ms</div>
          </div>
        </div>
      </div>

      {/* Recent Errors */}
      {stats.recentErrors && stats.recentErrors.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex items-center gap-3 mb-6 pb-4 border-b">
            <AlertCircle className="w-6 h-6 text-red-500" />
            <h3 className="text-xl font-bold text-gray-900">Recent Errors</h3>
            <span className="ml-auto px-3 py-1 bg-red-100 text-red-700 text-sm font-semibold rounded-full">
              {stats.recentErrors.length} errors
            </span>
          </div>
          <div className="space-y-3 max-h-[500px] overflow-y-auto">
            {[...stats.recentErrors].reverse().slice(0, 20).map((err, i) => (
              <div key={i} className="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors">
                <div className="flex justify-between items-start mb-2">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-sm font-bold text-red-700">#{err.requestId}</span>
                    <span className="px-2 py-0.5 bg-red-200 text-red-800 text-xs font-semibold rounded">
                      {err.errorType}
                    </span>
                  </div>
                  <span className="text-red-600 font-semibold text-sm">{err.latencyMs}ms</span>
                </div>
                <div className="text-gray-700 text-sm font-mono bg-white px-3 py-2 rounded border border-red-100">
                  {err.errorMessage}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
