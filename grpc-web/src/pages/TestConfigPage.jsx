import React from 'react';
import { Play, Save, StopCircle } from 'lucide-react';
import ServiceConfig from '../components/ServiceConfig';
import LoadProfile from '../components/LoadProfile';
import PayloadFields from '../components/PayloadFields';
import TestConsole from '../components/TestConsole';
import MetadataConfig from '../components/MetadataConfig';
import ResponseQueue from '../components/ResponseQueue';

export default function TestConfigPage(props) {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-8">
        <h2 className="text-3xl font-bold card-title">Configure Test</h2>
        <p className="text-gray-500 mt-1">Set up your gRPC load test parameters</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column */}
        <div className="space-y-6">
          <ServiceConfig
            onProtoUpload={props.handleProtoUpload}
            endpoint={props.endpoint}
            setEndpoint={props.setEndpoint}
            services={props.services}
            selectedService={props.selectedService}
            setSelectedService={props.setSelectedService}
            methods={props.methods}
            setMethods={props.setMethods}
            selectedMethod={props.selectedMethod}
            onMethodChange={props.handleMethodChange}
          />

          <LoadProfile
            serverConfig={props.serverConfig}
            mode={props.mode}
            setMode={props.setMode}
            vusers={props.vusers}
            setVusers={props.setVusers}
            duration={props.duration}
            setDuration={props.setDuration}
            rps={props.rps}
            setRps={props.setRps}
            timeout={props.timeout}
            setTimeout={props.setTimeout}
            workerThreads={props.workerThreads}
            setWorkerThreads={props.setWorkerThreads}
          />

          <MetadataConfig
            metadata={props.metadata}
            setMetadata={props.setMetadata}
          />

          <div className="flex flex-col gap-3">
            {props.running ? (
              <button
                onClick={props.stopTest}
                className="w-full py-3.5 bg-red-500 hover:bg-red-600 text-white rounded-lg font-semibold flex items-center justify-center gap-2 shadow-lg transition-all"
              >
                <StopCircle className="w-5 h-5" /> Stop Test
              </button>
            ) : (
              <button
                onClick={props.startTest}
                disabled={!props.selectedMethod}
                className="w-full py-3.5 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white rounded-lg font-semibold disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 shadow-lg transition-all"
              >
                <Play className="w-5 h-5" /> Start Test
              </button>
            )}

            <button
              onClick={props.saveSimulation}
              disabled={!props.selectedMethod}
              className="w-full py-3 bg-primary hover:bg-primary/90 text-white rounded-lg font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 transition-colors"
            >
              <Save className="w-5 h-5" /> Save Simulation
            </button>
          </div>
        </div>

        {/* Right Column */}
        <div className="lg:col-span-2 space-y-6">
          <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
            <h3 className="text-lg font-semibold mb-6 card-title border-b pb-3">Payload Fields</h3>
            <PayloadFields
              fields={props.fields}
              setFields={props.setFields}
              serverConfig={props.serverConfig}
            />
          </div>

          <div className="card rounded-xl shadow-sm border border-gray-100 p-6">
            <h3 className="text-lg font-semibold mb-6 card-title border-b pb-3">Test Console</h3>
            <TestConsole stats={props.stats} running={props.running} />
          </div>

          <ResponseQueue stats={props.stats} />
        </div>
      </div>
    </div>
  );
}
