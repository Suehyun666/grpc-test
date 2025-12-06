import API_BASE_URL from './config';

export const configService = {
  async getServerConfig() {
    const response = await fetch(`${API_BASE_URL}/config`);
    return response.json();
  }
};
