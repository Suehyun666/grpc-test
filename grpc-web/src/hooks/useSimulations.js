import { useState, useEffect } from 'react';
import { simulationService } from '../api';

export function useSimulations() {
  const [simulations, setSimulations] = useState([]);

  useEffect(() => {
    loadSimulations();
  }, []);

  const loadSimulations = async () => {
    try {
      const sims = await simulationService.getSimulations();
      setSimulations(sims);
    } catch (err) {
      console.error('Failed to load simulations:', err);
    }
  };

  const deleteSimulation = async (id) => {
    await simulationService.deleteSimulation(id);
    setSimulations(simulations.filter(s => s.id !== id));
  };

  return {
    simulations,
    deleteSimulation,
    refreshSimulations: loadSimulations
  };
}
