import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthTokens } from '../domain/AuthTokens';
import type { User } from '../domain/User';
import type { AuthResult } from '../domain/AuthRepository';

interface AuthState {
  user: User | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  setSession: (result: AuthResult) => void;
  clear: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      tokens: null,
      isAuthenticated: false,
      setSession: ({ user, tokens }) => set({ user, tokens, isAuthenticated: true }),
      clear: () => set({ user: null, tokens: null, isAuthenticated: false }),
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ user: state.user, tokens: state.tokens }),
      // Recompute the derived flag from persisted tokens on rehydrate.
      onRehydrateStorage: () => (state) => {
        if (state) {
          state.isAuthenticated = !!state.tokens?.accessToken;
        }
      },
    },
  ),
);
