/**
 * Reads the `exp` claim from a JWT without verifying its signature (the server
 * remains the source of truth). Used to treat a present-but-expired access
 * token as no session, so a reload routes to /login instead of flashing the app.
 */
export function isAccessTokenExpired(accessToken: string | undefined): boolean {
  if (!accessToken) return true;
  try {
    const [, payload] = accessToken.split('.');
    const { exp } = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    if (typeof exp !== 'number') return false;
    return exp * 1000 <= Date.now();
  } catch {
    return false;
  }
}