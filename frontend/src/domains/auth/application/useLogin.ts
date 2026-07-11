import { useMutation } from '@tanstack/react-query';
import { authApi } from '../infrastructure/authApi';
import type { LoginCredentials } from '../domain/AuthRepository';
import { useAuthStore } from './authStore';

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (credentials: LoginCredentials) => authApi.login(credentials),
    onSuccess: (result) => setSession(result),
  });
}
