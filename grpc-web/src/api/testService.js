import API_BASE_URL from './config';

export const testService = {
  /**
   * Start a test scenario
   * @param {Object} scenario - Test scenario configuration
   * @returns {Promise<{status: string, message: string}>}
   */
  async startTest(scenario) {
    const response = await fetch(`${API_BASE_URL}/start`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(scenario)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Failed to start test");
    return data;
  },

  /**
   * Stop the running test
   * @returns {Promise<{status: string}>}
   */
  async stopTest() {
    const response = await fetch(`${API_BASE_URL}/stop`, { 
      method: 'POST' 
    });
    if (!response.ok) throw new Error("Failed to stop test");
    return response.json();
  },

  /**
   * Get basic statistics
   * @returns {Promise<Object>}
   */
  async getStats() {
    const response = await fetch(`${API_BASE_URL}/stats`);
    return response.json();
  },

  /**
   * Get detailed statistics
   * @returns {Promise<Object>}
   */
  async getDetailedStats() {
    const response = await fetch(`${API_BASE_URL}/stats/detailed`);
    return response.json();
  }
};
