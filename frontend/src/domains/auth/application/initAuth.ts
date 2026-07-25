import { configureHttpAuth } from '@/shared/api/httpClient';
import { authApi } from '../infrastructure/authApi';
import { useAuthStore } from './authStore';

/**
 * Wire the shared HTTP client to the auth store. Called once at app startup so
 * that `shared/` never has to import the auth domain directly.
 */
export function initAuth(): void {
  configureHttpAuth({
    getAccessToken: () => useAuthStore.getState().tokens?.accessToken ?? null,

    refreshAccessToken: async () => {
      const refreshToken = useAuthStore.getState().tokens?.refreshToken;
      if (!refreshToken) return null;
      try {
        const result = await authApi.refresh(refreshToken);
        useAuthStore.getState().setSession(result);
        return result.tokens.accessToken;
      } catch {
        useAuthStore.getState().clear();
        return null;
      }
    },

    // Wipe the session; ProtectedRoute reacts to isAuthenticated and routes to /login.
    onUnauthorized: () => useAuthStore.getState().clear(),
  });
}
