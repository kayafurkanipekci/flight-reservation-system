import { createContext, useContext, useState, type ReactNode } from 'react';
import { apiClient } from '../api/client';

export interface AuthContextType {
  token: string | null;
  email: string | null;
  role: string | null;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, firstName: string, lastName: string, phoneNumber: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [email, setEmail] = useState<string | null>(() => localStorage.getItem('email'));
  const [role, setRole] = useState<string | null>(() => localStorage.getItem('role'));
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (email: string, password: string) => {
  setIsLoading(true);
  setError(null);
  try {
    const response = await apiClient.post('/auth/login', { email, password });
    const { token, email: userEmail, role } = response.data;

    localStorage.setItem('token', token);
    localStorage.setItem('email', userEmail);
    localStorage.setItem('role', role);

    setToken(token);
    setEmail(userEmail);
    setRole(role);
  } catch (err: any) {
    const message = err.response?.data?.message || 'Login failed';
    setError(message);
    throw err;
  } finally {
    setIsLoading(false);
  }
};

  const register = async (
    email: string,
    password: string,
    firstName: string,
    lastName: string,
    phoneNumber: string
  ) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await apiClient.post('/auth/register', {
        email,
        password,
        firstName,
        lastName,
        phoneNumber,
      });
      const { token, email: userEmail, role } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('email', userEmail);
      localStorage.setItem('role', role);

      setToken(token);
      setEmail(userEmail);
      setRole(role);
    } catch (err: any) {
      const message = err.response?.data?.message || 'Registration failed';
      setError(message);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    setToken(null);
    setEmail(null);
    setRole(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        email,
        role,
        isLoading,
        error,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};