import { useState, useEffect } from 'react';
import { fileService, testService, simulationService, configService } from '../api';

export function useTestConfig() {
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
  const [workerThreads, setWorkerThreads] = useState(0); // 0 = auto
  const [metadata, setMetadata] = useState({});

  const [fields, setFields] = useState([]);
  const [running, setRunning] = useState(false);
  const [stats, setStats] = useState(null);

  useEffect(() => {
    configService.getServerConfig()
      .then(setServerConfig)
      .catch(err => console.error('Failed to load server config:', err));
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
        .then(stats => {
          setStats(stats);
          
          // 디버깅용 로그
          console.log('📊 Stats received:', { 
            isRunning: stats.isRunning, 
            mode, 
            successCount: stats.successCount,
            failCount: stats.failCount
          });
          
          // 핵심: 백엔드가 멈췄으면 (isRunning: false) 프론트엔드도 멈춤
          // SINGLE만 응답으로 처리하고, 나머지는 다 여기서 멈춤
          if (stats.isRunning === false && mode !== 'SINGLE') {
            console.log('🛑 Server signaled finish. Stopping UI.');
            setRunning(false);
          }
        })
        .catch(err => console.error('Failed to get stats:', err));
    }, 1000);
    return () => clearInterval(interval);
  }, [running, mode]);

  const handleProtoUpload = async (files) => {
    if (!files || files.length === 0) return;

    setProtoFile(files[0]);

    try {
      const protoInfo = await fileService.uploadProto(files);
      setServerProtoPath(protoInfo.path);

      if (protoInfo.services && protoInfo.services.length > 0) {
        const firstService = protoInfo.services[0];
        setServices(protoInfo.services);
        setSelectedService(firstService.name);

        if (firstService.methods && firstService.methods.length > 0) {
          const firstMethod = firstService.methods[0];
          setMethods(firstService.methods.map(m => m.name));
          setSelectedMethod(firstMethod.name);
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
    setStats(null);

    // 검증: duration < timeout인 경우 경고 (BURST 모드 제외 - duration 불필요)
    if (mode !== 'SINGLE' && mode !== 'BURST' && duration > 0 && timeout > 0 && duration < timeout) {
      const proceed = window.confirm(
        `Warning: Duration (${duration}s) is shorter than Timeout (${timeout}s).\n` +
        `Requests may not complete before the test ends.\n\n` +
        `Do you want to continue?`
      );
      if (!proceed) return;
    }

    // workerThreads 계산: 0이면 vusers 기반 자동 계산, 아니면 설정값 사용
    const calculatedWorkerThreads = workerThreads > 0 ? workerThreads : Math.max(4, Math.min(vusers, 32));

    const finalProtoPath = serverProtoPath || protoFile?.name;
    const scenario = {
      protoFilePath: finalProtoPath,
      serviceName: selectedService,
      methodName: selectedMethod,
      endpoint,
      timeoutSec: timeout,
      metadata: metadata,
      loadProfile: { mode, virtualUsers: vusers, durationSec: duration, targetRps: rps, workerThreads: calculatedWorkerThreads },
      fieldRules: fields.reduce((acc, f) => {
        if (f.mode === 'fixed') acc[f.name] = { type: 'FIXED', value: f.value };
        else if (f.mode === 'random_int') acc[f.name] = { type: 'RANDOM_INT', minValue: f.min, maxValue: f.max };
        else if (f.mode === 'uuid') acc[f.name] = { type: 'UUID' };
        else if (f.mode === 'sequence') acc[f.name] = { type: 'SEQUENCE', minValue: f.min, maxValue: f.max };
        else if (f.mode === 'round_robin') acc[f.name] = { type: 'ROUND_ROBIN', minValue: f.min, maxValue: f.max };
        return acc;
      }, {})
    };

    setRunning(true);

    try {
      const result = await testService.startTest(scenario);

      if (result.status === 'completed') {
        setStats(result.stats);
      }
      
      // SINGLE만 즉시 종료합니다. 
      // BURST는 "Started" 상태만 받고 백그라운드에서 돌기 때문에 여기서 끄면 안 됩니다.
      // 폴링에서 isRunning: false 신호를 받을 때까지 계속 실행 중으로 유지합니다.
      if (mode === 'SINGLE') {
        setRunning(false);
      }
    } catch (error) {
      console.error('Test failed:', error);
      alert('Error: ' + error.message);
      setRunning(false);
    }
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
  };

  return {
    serverConfig,
    protoFile,
    services,
    selectedService,
    setSelectedService,
    methods,
    setMethods,
    selectedMethod,
    endpoint,
    setEndpoint,
    mode,
    setMode,
    vusers,
    setVusers,
    duration,
    setDuration,
    rps,
    setRps,
    timeout,
    setTimeout,
    workerThreads,
    setWorkerThreads,
    metadata,
    setMetadata,
    fields,
    setFields,
    running,
    stats,
    handleProtoUpload,
    handleMethodChange,
    startTest,
    stopTest,
    saveSimulation
  };
}
