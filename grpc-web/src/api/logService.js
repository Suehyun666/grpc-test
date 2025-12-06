import API_BASE_URL from './config';

export const logService = {
  async listLogs() {
    const response = await fetch(`${API_BASE_URL}/logs`);
    return response.json();
  },

  async getLogContent(filename, tail = 100) {
    const response = await fetch(`${API_BASE_URL}/logs/${filename}?tail=${tail}`);
    return response.text();
  }
};
