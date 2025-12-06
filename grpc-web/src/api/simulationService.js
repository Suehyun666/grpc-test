import API_BASE_URL from './config';

export const simulationService = {
  /**
   * Get all saved simulations
   * @returns {Promise<Array>}
   */
  async getSimulations() {
    const response = await fetch(`${API_BASE_URL}/simulations`);
    return response.json();
  },

  /**
   * Save a new simulation
   * @param {string} title - Simulation title
   * @param {Object} scenario - Test scenario
   * @returns {Promise<Object>}
   */
  async saveSimulation(title, scenario) {
    const response = await fetch(`${API_BASE_URL}/simulations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, scenario })
    });

    return response.json();
  },

  /**
   * Delete a simulation
   * @param {number} id - Simulation ID
   * @returns {Promise<void>}
   */
  async deleteSimulation(id) {
    await fetch(`${API_BASE_URL}/simulations/${id}`, {
      method: 'DELETE'
    });
  }
};
