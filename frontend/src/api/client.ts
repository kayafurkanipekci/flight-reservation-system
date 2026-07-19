import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - her requeste token'ı ekle
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - 401'de logout yap (ama login/register hariç)
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Login ve register endpoint'lerinde error mesajını göster, redirect yapma
    const isAuthEndpoint = 
      error.config?.url?.includes('/auth/login') || 
      error.config?.url?.includes('/auth/register');

    if (error.response?.status === 401 && !isAuthEndpoint) {
      // Diğer endpoint'lerde 401 = token süresi doldu, logout yap
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      localStorage.removeItem('role');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);