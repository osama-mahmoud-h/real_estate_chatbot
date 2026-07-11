import axios, {
  AxiosError,
  type AxiosInstance,
  type InternalAxiosRequestConfig,
} from 'axios';
import { env } from '@/shared/config/env';
import type { ApiResponse } from './ApiResponse';

/**
 * Auth is injected (not imported) so this shared module never depends on the
 * auth domain. The auth layer calls `configureHttpAuth` once at startup.
 */
type TokenProvider = () => string | null;
type RefreshFn = () => Promise<string | null>;
type UnauthorizedFn = () => void;

let getAccessToken: TokenProvider = () => null;
let refreshAccessToken: RefreshFn = async () => null;
let onUnauthorized: UnauthorizedFn = () => {};

export function configureHttpAuth(opts: {
  getAccessToken: TokenProvider;
  refreshAccessToken: RefreshFn;
  onUnauthorized: UnauthorizedFn;
}): void {
  getAccessToken = opts.getAccessToken;
  refreshAccessToken = opts.refreshAccessToken;
  onUnauthorized = opts.onUnauthorized;
}

export const httpClient: AxiosInstance = axios.create({
  baseURL: env.apiPrefix,
  headers: { 'Content-Type': 'application/json' },
});

// Attach the bearer token to every outgoing request.
httpClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

interface RetriableConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

// Single-flight refresh: concurrent 401s share one refresh request.
let refreshPromise: Promise<string | null> | null = null;

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const original = error.config as RetriableConfig | undefined;
    const status = error.response?.status;

    const isAuthEndpoint = original?.url?.includes('/auth/');

    if (status === 401 && original && !original._retry && !isAuthEndpoint) {
      original._retry = true;

      if (!refreshPromise) {
        refreshPromise = refreshAccessToken().finally(() => {
          refreshPromise = null;
        });
      }

      const newToken = await refreshPromise;

      if (newToken) {
        original.headers.Authorization = `Bearer ${newToken}`;
        return httpClient(original);
      }

      // Refresh failed → session is dead. Let the auth layer route to /login.
      onUnauthorized();
    }

    return Promise.reject(error);
  },
);

/** Unwrap the `MyApiResponse<T>` envelope, returning just `data`. */
export function unwrap<T>(response: { data: ApiResponse<T> }): T {
  return response.data.data;
}

/** Extract a human-readable message from any thrown request error. */
export function toApiErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiResponse<unknown> | undefined;
    return data?.message || data?.error?.details || error.message || fallback;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
