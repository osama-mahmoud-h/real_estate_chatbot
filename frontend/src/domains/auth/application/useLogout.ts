import { useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from './authStore';

/** Clears the session, wipes cached server state, and routes to /login. */
export function useLogout() {
  const clear = useAuthStore((s) => s.clear);
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useCallback(() => {
    clear();
    queryClient.clear();
    navigate('/login', { replace: true });
  }, [clear, queryClient, navigate]);
}
