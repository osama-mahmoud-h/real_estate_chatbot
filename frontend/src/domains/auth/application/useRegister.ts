import { useMutation } from '@tanstack/react-query';
import { authApi } from '../infrastructure/authApi';
import type { RegisterPayload } from '../domain/AuthRepository';
import { useAuthStore } from './authStore';

export function useRegister() {
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (payload: RegisterPayload) => authApi.register(payload),
    onSuccess: (result) => setSession(result),
  });
}
