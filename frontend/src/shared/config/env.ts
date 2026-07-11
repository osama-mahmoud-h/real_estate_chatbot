const rawBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

// Normalize: strip trailing slashes so we can safely append paths.
const baseUrl = rawBaseUrl.replace(/\/+$/, '');

export const env = {
  /** Backend origin, e.g. http://localhost:8080 */
  apiBaseUrl: baseUrl,
  /** Versioned API prefix used by every request. */
  apiPrefix: `${baseUrl}/api/v1`,
} as const;
