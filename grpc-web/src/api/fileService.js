import API_BASE_URL from './config';

export const fileService = {
  async uploadProto(files) {
    const formData = new FormData();

    if (Array.isArray(files)) {
      files.forEach(file => formData.append('files', file));
    } else {
      formData.append('files', files);
    }

    const response = await fetch(`${API_BASE_URL}/upload`, {
      method: 'POST',
      body: formData
    });

    if (!response.ok) {
      throw new Error('Failed to upload proto file');
    }

    return response.json();
  }
};
